package org.docksidestage.handson.exercise;



import java.time.LocalDate;

import javax.annotation.Resource;

import org.dbflute.cbean.result.ListResultBean;
import org.docksidestage.handson.dbflute.exbhv.MemberBhv;
import org.docksidestage.handson.dbflute.exentity.Member;
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
        LocalDate birthdate = LocalDate.of(1968, 1, 1);
        //Dataがたくさんある！😭
        //java.sql.Dateかと思ったらコンパイルエラーになった

        // Act
        //&検索なので、orはつけない
        //複数検索なのでListで取得
        ListResultBean<Member> memberList = memberBhv.selectList(cb -> {
            //セクション２でやったように曖昧検索
            cb.query().setMemberName_LikeSearch(prefix, op -> op.likePrefix());
            //生年月日が指定した日付よりも下であることを条件に加える
            cb.query().setBirthdate_LessEqual(birthdate);
            cb.query().addOrderBy_Birthdate_Asc();
            //会員ステータスも取得する
            cb.setupSelect_MemberStatus();
        });

        // Assert
        assertFalse(memberList.isEmpty());
        for (Member member : memberList) {
            assertTrue(member.getMemberName().startsWith(prefix));
            //該当日も含めるので0以下であることをアサート
            assertTrue(member.getBirthdate().compareTo(birthdate) <= 0);
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
            //会員のステータスとセキュリティ情報を取得する
            cb.setupSelect_MemberStatus();
            cb.setupSelect_MemberSecurityAsOne();
            //若い順＝生年月日新しい降順で並べる.最後にnullを持ってくる
            cb.query().addOrderBy_Birthdate_Desc().withNullsLast();
            //生年月日がない人は会員IDの昇順で並ぶようにする
            cb.query().addOrderBy_MemberId_Asc();
        });

        // Assert
        assertFalse(memberList.isEmpty());
        for (Member member : memberList) {
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
            //会員セキュリティ情報のリマインダ質問で2という文字が含まれている会員を検索
            cb.query().queryMemberSecurityAsOne().setReminderQuestion_LikeSearch(containsStr, op -> op.likeContain());
        });

        // Assert
        //一回りマインドの質問が含まれているものを全件持ってきて、そこからフィルタリングして２を見つけたい
        assertFalse(memberList.isEmpty());
        for (Member member : memberList) {
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
