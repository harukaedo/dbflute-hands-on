package org.docksidestage.handson.exercise;



import java.time.LocalDate;

import javax.annotation.Resource;

import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.optional.OptionalEntity;
import org.docksidestage.handson.dbflute.exbhv.MemberBhv;
import org.docksidestage.handson.dbflute.exentity.Member;
import org.docksidestage.handson.dbflute.exentity.MemberStatus;
import org.docksidestage.handson.unit.UnitContainerTestCase;

/**
 * @author harukaedo
 */
public class HandsOn03Test extends UnitContainerTestCase {
    
    @Resource
    private MemberBhv memberBhv;

    /*
    Silverストレッチ①
    会員名称がSで始まる1968年1月1日以前に生まれた会員を検索
    - 会員ステータスも取得する
    - 生年月日の昇順で並べる
    - 会員が1968/01/01以前であることをアサート(※"以前" の解釈は、"その日ぴったりも含む" で。)
     */
    public void test_memberNameStartsWithSAndBirthdateBefore19680101() throws Exception {
        // Arrange
        //会員の検索条件であり、会員名称と生年月日を指定するための変数を用意しておく
        String prefix = "S";
        // #1on1: 1968/1/1 自体が birthdate そのものか？話 (2026/05/29)
        // 概念力の話。遵守性は高い方が良いですよ。
        LocalDate birthdate = LocalDate.of(1968, 1, 1);
        //Dateがたくさんある！😭
        //java.sql.Dateかと思ったらコンパイルエラーになった
        // #1on1: java.util.Date, java.sql.Date とパッケージ違いの同名クラス (2026/05/29)
        // 補完ノイズ。ほとんど util.Date しか使わないのに。
        // packageでユニークにしても、クラス名である程度のユニーク性が欲しい話。
        // DBのテーブル名とカラム名での例のお話。MEMBER_NAME? or NAME?
        //
        // #1on1: 一方で、java.util.Dateは古いクラスで、今はLocalDateを使っている。
        // これは、java.util.Date系のクラスが残念な実装になっててみんなイヤだったので...
        // 新しく作り直したLocalDateが登場。でも古いクラスは消せないので残っている。
        // (Javaは(できるだけ)互換性をキープすることをポリシーとしている)
        // (Javaのオフィシャル自体もそういう冒険をしちゃうもの)

        // Act
        //&検索なので、orはつけない
        //複数検索なのでListで取得
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
            //セクション２でやったように曖昧検索
            cb.query().setMemberName_LikeSearch(prefix, op -> op.likePrefix());
            //生年月日が指定した日付よりも下であることを条件に加える
            cb.query().setBirthdate_LessEqual(birthdate);
            cb.query().addOrderBy_Birthdate_Asc();
            // TODO edo setupSelectはselect句のメソッドなので、一番上に定義したい by jflute (2026/05/29)
            // これはDBFluteの提案的慣習で、別に必須ではないけど、みんながそうやれば可読性が良くなる。
            //会員ステータスも取得する
            cb.setupSelect_MemberStatus();
        });

        // Assert
        assertFalse(memberList.isEmpty());
        for (Member member : memberList) {
        	// TODO edo 複数 get...() している箇所を変数化してみましょう。変数名いい感じに by jflute (2026/05/29)
        	// #1on1: assertを目視確認するために、log()を自由に出しても良い (2026/05/29)
        	log(member.getMemberName(), member.getBirthdate());

            assertTrue(member.getMemberName().startsWith(prefix));
            //該当日も含めるので0以下であることをアサート
            assertTrue(member.getBirthdate().compareTo(birthdate) <= 0);
            // TODO edo getMemberStatus()はJavaDoc見るとnullを戻さないので意味のないアサートになってる by jflute (2026/05/29)
            // 「会員ステータスを取得したか？」の判定手段がnullではないということ。
            // OptionalEntity がemptyか？emptyじゃないか？で判定しないといけない。
            // #1on1: UnitTestのアサートを書く時の慣習、いっかい落としてアサート自体を確認しましょう (2026/05/29)
			assertNotNull(member.getMemberStatus());
        }
    }

    /*
    Silverストレッチ②
    会員ステータスと会員セキュリティ情報も取得して会員を検索
    - 若い順で並べる。生年月日がない人は会員IDの昇順で並ぶようにする
    - 会員ステータスと会員セキュリティ情報が存在することをアサート
    ※カージナリティを意識しましょう
     */
    //若い順に並べた会員と、会員IDない人はどっちを先に並べるのか
    //特段定義されていないが、生年月日がNullという扱いにして最後に持ってくるようにする
    //イメージ→　年齢層（低）→ 年齢層（高）→　生年月日がない人
    public void test_memberWithMemberStatusAndMemberSecurity() throws Exception {
        // Arrange
        //特に定義は必要なさそう

        // Act
        //複数検索なのでListで取得
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
        	// TODO jflute 次回1on1, カージナリティ (2026/05/29)
            //会員のステータスとセキュリティ情報を取得する
            cb.setupSelect_MemberStatus();
            cb.setupSelect_MemberSecurityAsOne();
            // #1on1: 第一ソートキーだけでは、ユニークに並ばない話 (2026/05/29)
            // 同じ生年月日の会員同士は、どう並べればいいの？問題。
            // ユニークにならないと何が良くないか？ → 検索のたびに順序が変わる可能性
            // 画面としては、データが変わってないのに見た目が変わるってあまりUI的に良くない。
            // だからこそ第二ソートキーがある。(固定化のためだけの最後PKソートってこともある)
            //若い順＝生年月日新しい降順で並べる.最後にnullを持ってくる
            cb.query().addOrderBy_Birthdate_Desc().withNullsLast();
            //生年月日がない人は会員IDの昇順で並ぶようにする
            cb.query().addOrderBy_MemberId_Asc();
        });

        // Assert
        assertFalse(memberList.isEmpty());
        for (Member member : memberList) {
        	// TODO edo assertになってない (null検査ではない) by jflute (2026/05/29)
            assertNotNull(member.getMemberStatus());
            assertNotNull(member.getMemberSecurityAsOne());
        }
    }

    /*
    Silverストレッチ③
    会員セキュリティ情報のリマインダ質問で2という文字が含まれている会員を検索
    - 会員セキュリティ情報のデータは不要
    - (Actでの検索は本番でも実行されることを想定し、テスト都合でパフォーマンス劣化させないこと)
    - リマインダ質問に2が含まれていることをアサート
    - アサートするために別途検索処理を入れても誰も文句は言わない
     */
    public void test_reminderQuestion_contains_2() throws Exception {
        // Arrange
        //リマインダ質問に2が含まれていることをアサートするための変数を用意しておく
        String containsStr = "2";

        // Act
        //複数検索なのでListで取得
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
        	// #1on1: where句のためのjoin (無駄なデータは取ってこない) (2026/05/29)
        	// joinというのは、基本的には何かの目的を達成するための手段。
        	// select句のためのjoin, where句のためのjoin, order by句のためのjoin
        	// joinだけして嬉しいことはそんなにない。(文法的には色々できるけど基本的には)
        	//
            //会員セキュリティ情報のリマインダ質問で2という文字が含まれている会員を検索
            cb.query().queryMemberSecurityAsOne().setReminderQuestion_LikeSearch(containsStr, op -> op.likeContain());
        });

        // Assert
        //一回りマインドの質問が含まれているものを全件持ってきて、そこからフィルタリングして２を見つけたい
        assertFalse(memberList.isEmpty());
        for (Member member : memberList) {
        	// TODO edo トレーニング: MEMBERをもっかい検索する必要はなく... by jflute (2026/05/29)
        	// 会員セキュリティだけを検索するようにしてみましょう。memberSecurityBhv を連れてきて...
            //会員セキュリティ情報のデータは不要なので、別途検索処理を入れる
            Member memberWithSecurity = memberBhv.selectEntityWithDeletedCheck(cb -> {
                cb.query().setMemberId_Equal(member.getMemberId());
                cb.setupSelect_MemberSecurityAsOne();
            });
            //もう一回アサートしちゃう
            String reminderQuestion = memberWithSecurity.getMemberSecurityAsOne().get().getReminderQuestion();
            assertTrue(reminderQuestion.contains(containsStr));
        }
    }
}
