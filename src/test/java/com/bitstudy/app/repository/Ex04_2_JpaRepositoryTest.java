package com.bitstudy.app.repository;

import com.bitstudy.app.config.JpaConfig;
import com.bitstudy.app.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** TDD를 위해서 임시로 만들어 놓은 저장소(이것으로 DB 접근)
 *
 * - TDD 만드는 방법
 *   1) 클래스 이름에서 우클릭 > Go to > Test(ctrl + shift + T)
 *   2) JUnit5 버전인지 확인
 *   3) Test에 경로가 같게 생성되는지 확인 후 ok
 */
@DisplayName("JPA 테스트")
@DataJpaTest // 슬라이드 테스트
/* 슬라이드 테스트(유닛테스트?)란 지난번에 TDD때 각 메서드들은 다 구분되어 서로를 알아보지 못하게 만들었다.
   이것처럼 메서드들 각각 테스트한 결과(레이어마다)를 서로 못보게 잘라서 만드는것
   Test에서 DB 사용후 원래대로 돌리는 작업까지 같이 해준다.
   @ExtendWith룰 써줘야하는데 DataJpaTest가 가지고 있다. @Autowired 안써줘도 된다.
   */

@Import(JpaConfig.class)
/* 원래대로라면 JPA에서 모든 정보를 컨트롤해야하는데 JpaConfig의 경우 읽어오지 못한다.
   이거는 시스템에서 만든게 아니라 우리가 별도로 만든 파일이기 때문이다. 그래서 별도로 import 한것
   안하면 config안에 명시해놨던 JpaAuditing이 동작하지 않는다. */
class Ex04_2_JpaRepositoryTest {
    private final Ex04_ArticleRepository_기본테스트용 articleRepository;

    private final Ex05_ArticleCommentRepository_기본테스트용 articleCommentRepository;


    /* 원래는 둘다 @Autowired가 붙어야 하는데, JUnit5 버전과 최신버전의 스프링 부트를 이용하면 Test에서 생성자 주입패턴 사용할수 있다.*/

    /* 생성자 만들기 - 여기서는 다른 파일에서 매개변수로 보내주는걸 받는거라서 위에랑 상관없이 @Autowired를 붙여야함  */
    public Ex04_2_JpaRepositoryTest(@Autowired Ex04_ArticleRepository_기본테스트용 articleRepository, @Autowired Ex05_ArticleCommentRepository_기본테스트용 articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    /* - 트랜잭션 시 사용하는 메서드(DB와 왔다갔다하는) - JPA에서는 쿼리문 안쓸거다 다음 함수로할거다
         사용법: repository명.메소드()
         1) findAll() - 모든 컬럼을 조회할때 사용, 페이징(pageable) 가능
                        당연히 select 작업을 하지만 잠깐 사이에 테이블에 어떤 변화가 있었는지 알 수 없기 때문에
                        select전에 먼저 최신 데이터를 잡기 위해서 update를 한다.
                        동작순서 : update -> select
         2) findById() - 한건에 대한 데이터 조회시 사용
                         primary key로 레코드 한 건 조회
                         ()안에 글번호를 넣어줘야 한다.
         3) save() - 레코드 저장할 때 사용(insert, update)
                   - saveAndFlush()
         4) count() - 레코드 개수 뽑을 때 사용
         5) delete() - 레코드 삭제
         ----------------------------------------------------------
         - 테스트용 데이터 가져오기
           1) https://www.mockaroo.com/ 사이트 접속
     */

    // 테스트 이후에 실제 DB에 정보가 변하지않는다 test는 다시 rollback한다.
    /** select 테스트 */
    @DisplayName("셀렉트 테스트")
    // 함수에 대한 별칭 달아주기
    @Test
    void selectTest() {
        long cnt = articleRepository.count();
        List<Article> articles = articleRepository.findAll();

//        assertThat(articles).isNotNull().hasSize((int) cnt);
        assertThat(articles).isNotNull().hasSize(100);

    }

    /** insert 테스트 */
    @DisplayName("삽입 테스트")
    @Test
    void insertTest() {
        long prevCnt = articleRepository.count();
        Article article = Article.of("제목1", "내용1", "#해시태그1");
        articleRepository.save(article);
        assertThat(articleRepository.count()).isEqualTo(prevCnt + 1);
    }

    @DisplayName("업데이트 테스트")
    @Test
    void updateTest() {
        Article article = articleRepository.findById(1L).orElseThrow();
        String updateHashtag = "#1234";
        article.setHashtag(updateHashtag);
        Article updatedArticle = articleRepository.saveAndFlush(article);
//        assertThat(updatedArticle).isEqualTo(updatedArticle);
        assertThat(updatedArticle).hasFieldOrPropertyWithValue("hashtag", updateHashtag);
    }

    @DisplayName("삭제 테스트")
    @Test
    void deleteTest() {
        Article article = articleRepository.findById(1L).orElseThrow();

        long prevArticleCnt = articleRepository.count();
        long prevArticleCommentCnt = articleCommentRepository.count();
        long deletedArticleComment = article.getArticleComments().size();

        articleRepository.delete(article);

        assertThat(articleRepository.count()).isEqualTo(prevArticleCnt - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(prevArticleCommentCnt - deletedArticleComment);
    }

}