package com.bitstudy.app.repository;

import com.bitstudy.app.config.JpaConfig;
import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

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
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;

    private final ArticleCommentRepository articleCommentRepository;
    /* 원래는 둘다 @Autowired가 붙어야 하는데, JUnit5 버전과 최신버전의 스프링 부트를 이용하면 Test에서 생성자 주입패턴 사용할수 있다.*/

    private final UserAccountRepository userAccountRepository;

    /* 생성자 만들기 - 여기서는 다른 파일에서 매개변수로 보내주는걸 받는거라서 위에랑 상관없이 @Autowired를 붙여야함  */
    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository,
                             @Autowired UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
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
        /** 셀렉트 할거니까 articleRepository 를 기준으로 테스트 할거임
         *  maven방식: dao -> mapper로 정보 보내고 DB갔다와서 Controller까지 돌려보낼건데 dao에서 DTO를 list에 담아서
         *  return을 시켜줬음
         *  gradle방식은 dao, mapper가 없고 Jpa(findAll())가 그 역할을 한다.
         *  db와 mapping을 해주기위한 framework
         *  DTO(Data Transfer Object)
         *
         *  garbage Collector 동작방식
         *  1. Garbage collector가 Stack의 모든 변수를 스켄하면서 각각 어떤 객체를 참조하고 있는지 찾아서 마킹
         *  2. Reachable Object(리스트 안에 객체, 스택 → 힙(리스트) →힙(객체))가 참조하고 있는 객체도 찾아서 마킹 (1, 2 번을 Mark라 부름)
         * 3. 마킹되지 않은 객체를 Heap에서 제거 (Sweep이라 부름)
         * 4. compact를 하는 GC라면, 단편화 된 메모리를 정리
         */

        List<Article> articles  = articleRepository.findAll();
        // Jpa에서는 사용자가 selectAll같이 이름을 정해줄수없다 정해진 findAll() 메서드를 쓸거다
        // 제네릭스의 역할 정해진 타입의 요소만 받을거야 다른거는 튕겨낼거야

        /* assertJ를 이용해서 테스트 할거임
           List에 담겨있는 Articles가 NotNull이고 사이즈가 ??개면 통과 */
        assertThat(articles).isNotNull().hasSize(100);
    }

    /** insert 테스트 */
    @Test
    void insertTest() {
    /* 기존의 article 갯수를 센 다음에 insert를 하고 기존거보다 현재것이 1차이가 나면
       insert가 제대로 된것 */
        // 기존 카운트 구하기
        long prevCount = articleRepository.count();

        // insert하기
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("bitstudy", "asdf", null, null, null));
        Article article = Article.of(userAccount, "제목1", "내용1", "#해시태그1");
        articleRepository.save(article);

        // 기존것과 현재거랑 개수 차이 구하기
        assertThat(articleRepository.count()).isEqualTo(prevCount+1);
    }

    @Test
    void updateTest() {
        /* 기존의 데이터 하나 있어야 되고, 그걸 수정했을때를 관찰할거임.
           1) 기존의 영속성 컨텍스트로부터 하나 엔티티 객체를 가져온다.(DB에서 한줄가져온다)
           2) 업데이트로 해시태그를 바꾸기
         */

        Article article = articleRepository.findById(1L).orElseThrow();

        String updateHashtag = "#abcd";
        article.setHashtag(updateHashtag);
//        articleRepository.save(article); // rollback 할거니 실제 업데이트안하고 확인만한다 그래서 flush 사용하여 실제 업데이트 해본다 그러나 결국에는 테스트니 다시 rollback
        Article savedArticle = articleRepository.saveAndFlush(article);

//        assertThat(savedArticle).hasFieldOrProperty();
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updateHashtag);
//        assertThat(savedArticle.getHashtag()).isEqualTo(updateHashtag);
    }

    @Test
    void deleteTest() {

        Article article = articleRepository.findById(1L).orElseThrow();

        /* 2) 지우면 DB에서 하나 사라지기 때문에 count를 구해놓고
              게시글(articleRepository) 뿐만아니라, 연관된 댓글(articleCommentRepository)
              까지 삭제할거라서 두개의 개수를 다 알아내기 */
        long prevArticleCount = articleRepository.count();
        long prevArticleCommnetCount = articleCommentRepository.count(); // 데이터베이스에 있는 모든 댓글의 수
        int deletedCommentSize = article.getArticleComments().size(); // 해당 게시글에 딸려있는 모든 댓글의 수
        // 나중에 "모든 댓글의 수 - 게시글에 딸려있는 댓글 수" 하면 몇개 지워졌는지 알 수 있다.
        System.out.println("deletedCommentSize = " + deletedCommentSize);

        /* 3) delete하고(전체 게시글 개수 -1됨) */
        articleRepository.delete(article);

        /* 테스트 통과 조건 - 2번에서 구한거랑 여기서 구하는 개수가 1차이 나는 경우 */
        assertThat(articleRepository.count()).isEqualTo(prevArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(prevArticleCommnetCount - deletedCommentSize);
    }

}