package org.docksidestage.handson.exercise;

// #1on1: packageのお話をした。packageを作って配置しましょう (2026/03/13)
// #1on1: docksidestage のお話。 (2026/04/10)
// #1on1: TestCaseの継承のお話。javatryと同じで、継承することでテストできるようになる。 (2026/03/13)
// #1on1: testメソッドの作成のお話。ここも基本的にjavatryと同じ (2026/03/13)
// #1on1: BehaviorでのselectCount()のライブコーディング (2026/03/13)
// (AWSのBehaviorという言葉と、ここでのBehaviorは全く別物)
// #1on1: assertの紹介、assertTrue()とassertEquals()の違い (2026/03/13)

// edo gitignoreに*.log入れてもらったけど、dbflute.logがまだgitに残ってる by jflute (2026/03/13)
// gitignoreに設定入れた時は、「これから追加されるlogファイルがignoreされる」というニュアンス。
// なので、すでに追加されているdbflute.logは変わらずまだgit管理になる。
// なのでなので、dbflute.logは明示的に削除コミットをしてあげないといけない。

import javax.annotation.Resource;

import org.dbflute.cbean.result.ListResultBean;
import org.docksidestage.handson.dbflute.exbhv.MemberBhv;
import org.docksidestage.handson.dbflute.exentity.Member;
import org.docksidestage.handson.unit.UnitContainerTestCase;

/**
 * @author harukaedo
 */

// done edo このテストクラスのpackageが、default package になってしまっているので... by jflute (2026/04/10)
// org.docksidestage.handson.exercise.HandsOn02Test になるようにしましょう。
// (つまり、org.docksidestage.handson.exercise という package に移動する)

//0420修正メモ：
//src/test/java/org/docksidestage/handson/exerciseを作成し、引っ越ししました

// TODO edo JavaDoc, javatryと同じようにauthorだけでもいいのでお願いします by jflute (2026/04/26)
public class HandsOn02Test extends UnitContainerTestCase {
    // よくわからないが、UnitContainerTestCase を継承する時に使うらしいので作っておく
    // section2が何も進んでおらずそれぞれのファイルの役割などがあまり理解できていないため解説していただけると助かります
    //0330修正メモ：dbflute_maihamadb/playsql/data/ut/reversetsv/UTF-8/defaultValueMap.datapropファイルの命名が
    //efaultValueMap.dataprop.tsvになっていたため修正。
    //命名してもなかなかテストが通らないなと思ったらReplaceSchemaができていなかった
    //ReplaceSchemaの役割がよくわかっていない。
    //defaultValueMap.datapropファイルがDBを入れる時の手順書みたいなもので、ReplaceSchemaがその手順書に沿って
    //データを入れ直すイメージ？？

	// #1on1: ReplaceSchema から UnitTest の実行まで (2026/04/10)
	// 1. 元々で言うと、ローカルMySQLには何もない (DBもデータもない)
	//
	// 2. ReplaceSchemaを実行すると、ローカルMySQLにスキーマを作成する＆tsvデータを登録する
	//    (ReplaceSchemaは、あくまでMySQLに対して作用するもので、Javaのプログラムに影響するじゃない)
	//    (ReplaceSchemaは、既存のスキーマとデータをいったん壊して、また作り直す)
	//
	// 3. UnitTest は Java でのプログラムの実行で、DBFluteを経由してMySQLにアクセス
	//    (UnitTestは、playsqlフォルダとかは全く見ない)
	//
	// セクション1では、ReplaceSchemaは実行したけど、まだtsvデータを登録してなかった
	// なので、その状態でUnitTestでMySQLにアクセスしても、取得できるデータは0件
	// 
	// defaultValueMap.dataprop は、ReplaceSchemaの機能一つ。
	// tsvデータのデフォルト値になるものなので、ReplaceSchemaを実行しないと全く評価されない。
	
	// TODO jflute どこかでstep7をやったら、ここの例外翻訳の話とつなげる (2026/04/10)

    @Resource
    private MemberBhv memberBhv;
    
    public void test_existsTestData() throws Exception {
        // Arrange
        // Act
        int count = memberBhv.selectCount(cb -> {});
        // Assert
        assertTrue(count > 0);
    }

    /*
    問題１
    会員名称がSで始まる会員を検索 (これはタイトル、この中にも要件が含まれている)
    会員名称の昇順で並べる (これは実装要件、Arrange or Act でこの通りに実装すること)
    (検索結果の)会員名称がSで始まっていることをアサート (これはアサート要件、Assert でこの通りに実装すること)
    "該当テストデータなし" や "条件間違い" 素通りgreenにならないように素通り防止を (今後ずっと同じ)
     */
    //これをみながらやるmemo
    //https://dbflute.seasar.org/ja/manual/function/ormapper/conditionbean/howto.html
    //0420メモ
    //ConditionBeanの役割が理解できておらずなかなか進めないので役割などを教えていただけると助かります🙏
    //
    // TODO edo [ふぉろー] ConditionBeanは、「条件 condition」を司るオブジェクトということで... by jflute (2026/04/26)
    // 「会員名称がSで始まる会員を検索」
    // ...な会員を検索 → memberBhv.selectList(...) // Behaviorが検索の入り口 (取得形式を指定、一覧？一件？)
    // 会員名称がSで始まる会員 → cb.query().setMemberName... // SQLで言うとwhere句
    // 会員名称の昇順で並べる → cb.query().addOrderBy...     // SQLで言うとorder by句
    //
    // 1. 基点テーブル何か？                // Behaviorを選ぶ
    // 2. 一件検索なのか？リスト検索なのか？   // Behaviorのメソッドを選ぶ
    // 3. 取得したい関連テーブルは何か？      // ConditionBeanでsetupSelect
    // 4. どんな絞り込み、並び替えをしたいか？ // ConditionBeanでquery()
    //
    public void test_member_name_start_s() throws Exception {
        // Arrange
        //ここで始まる言葉を先に定義しておきたい。
        String start = "S";
        
        // Act
        //ここで検索条件を指定したい
        //memo
        // protected static final String LIKE_PREFIX = "prefix";最初 → 前方一致検索
        // protected static final String LIKE_SUFFIX = "suffix";最後 → 後方一致検索
        // protected static final String LIKE_CONTAIN = "contain";含む → 部分一致検索
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
        	// #1on1: 3. 取得したい関連テーブルは何か？ (2026/05/08)
        	// 世の中のデータ通信のお話。海底ケーブルも。無駄にデータを取りたくない。なので選択する。
        	//cb.setupSelect_MemberStatus(); // 関連テーブルの会員ステータスも一緒に取得してくる
        	//cb.setupSelect_MemberSecurityAsOne(); // 関連テーブルの会員セキュリティも一緒に取得してくる

        	// #1on1: orって使うのか？ (2026/05/08)
        	// dfloc.MEMBER_ACCOUNT = 'Pixy' and ( B or C ) 
        	// A かつ B (B1 もしくは B2) かつ C (C1 もしくは C2)
        	// Javaだと if (A && (B1 || B2) && (C1 || C2)) {
        	/*
        	cb.query().setMemberAccount_Equal("Pixy");
        	cb.orScopeQuery(orCB -> {
				orCB.query().setMemberName_LikeSearch(start, op -> op.likePrefix());
				orCB.query().setBirthdate_IsNotNull();
			});
			*/
        	// #1on1: あいまい検索のお話。SQLで言うと、likeという演算子を使う dfloc.MEMBER_NAME like 'S%' (2026/05/08)
            cb.query().setMemberName_LikeSearch(start, op -> op.likePrefix());
            
            // #1on1: Notもあるよ (2026/05/08)
            //cb.query().setBirthdate_NotEqual("...");

            // #1on1: 2個目の絞り込み条件を指定すると、デフォルトでは and (かつ) になる。 (2026/05/08)
            //cb.query().setBirthdate_IsNotNull();
            
            // #1on1: order by: order by dfloc.MEMBER_NAME asc (2026/05/08)
            // asc:昇順、desc: 降順
            cb.query().addOrderBy_MemberName_Asc();
        });
        // #1on1: 一番大事なのは検索のイメージを頭の中で持つこと (2026/05/08)
        // どんな検索をしたいのか？を抽象的に捉えることができたら、後はSQLだったりCBだったり置き換えるだけ。
        // 「そもそも何をやりたいのか？ってのはツールとは無関係の世界で整理整頓できるはず」

        // Assert
        //for文とかでぐるぐる回して会員を引っこ抜いていきたい

        // #1on1: テストデータがちゃんと用意されていることをチェックしている (2026/05/08)
        assertFalse(memberList.isEmpty());//リストが空じゃないかをチェックしてくれる

        for (Member member : memberList) {
            String memberName = member.getMemberName();
            log(memberName);
            assertTrue(memberName.startsWith(start));   
        }
        //Savicevic,Stankovic,Stojkovic,Suker
    }

    /*  
    問題２
    会員IDが1の会員を検索
    一件検索として検索すること
    会員IDが 1 であることをアサート
    */
    public void test_member_id_1() throws Exception {
       // Arrange
    //指定したい会員idは何なのかを定義する。
    int memberId = 1;

       // Act
       //検索条件の指定。
       //一件検索なので、selectEntityWithDeletedCheckを使う。
    
    Member member = memberBhv.selectEntityWithDeletedCheck(cb -> {
        cb.query().setMemberId_Equal(memberId);
    });
       // Assert
    assertEquals(memberId, member.getMemberId());
    }

    //memo
    //selectEntityとselectEntityWithDeletedCheckの違い
    //selectEntityは、条件に合うものがなかったらnullを返す
    //selectEntityWithDeletedCheckは、条件に合うものがなかったら例外を投げる
    //今回の問題ではどっちを使うのが良いのかがあまり理解できていない。
    //会員IDという概念は存在するはずだから例外を投げてあげるほうが親切？

    /*
    生年月日がない会員を検索
    更新日時の降順で並べる
    生年月日がないことをアサート
     */
    public void test_birthdate_is_null() throws Exception {
        // Arrange
        //nullであればいいかを確認したいので特に定義はしなくても良さそう

        // Act
        //複数件検索なので、selectListを使う。
        //生年月日がない会員を検索 → cb.query().setBirthdate_IsNull();
        //更新日時の降順で並べる → cb.query().addOrderBy_UpdateDatetime_Desc();
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
            cb.query().setBirthdate_IsNull();
            cb.query().addOrderBy_UpdateDatetime_Desc();
        });

        // Assert
        //for文とかでぐるぐる回して会員を引っこ抜いていきたい
        assertFalse(memberList.isEmpty());

        for (Member member : memberList) {
            assertNull(member.getBirthdate());
        }
    }
}
