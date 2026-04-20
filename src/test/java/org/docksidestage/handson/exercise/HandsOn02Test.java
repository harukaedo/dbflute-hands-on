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

import org.docksidestage.handson.dbflute.exbhv.MemberBhv;
import org.docksidestage.handson.unit.UnitContainerTestCase;

// TODO done edo このテストクラスのpackageが、default package になってしまっているので... by jflute (2026/04/10)
// org.docksidestage.handson.exercise.HandsOn02Test になるようにしましょう。
// (つまり、org.docksidestage.handson.exercise という package に移動する)

//0420修正メモ：
//src/test/java/org/docksidestage/handson/exerciseを作成し、引っ越ししました

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
}
;