//package com.bitstudy.app.repository;
//
//import com.bitstudy.app.config.JpaConfig;
//import com.bitstudy.app.domain.Article;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//
//import java.util.List;
//import static org.assertj.core.api.Assertions.assertThat;
//
///** TDD를 위해서 임시로 만들어 놓은 저장소(이것으로 DB 접근)
// *
// * - TDD 만드는 방법
// *   1) 클래스 이름에서 우클릭 > Go to > Test(ctrl + shift + T)
// *   2) JUnit5 버전인지 확인
// *   3) Test에 경로가 같게 생성되는지 확인 후 ok
// */
//@DisplayName("JPA 테스트")
//@DataJpaTest // 슬라이드 테스트
///* 슬라이드 테스트(유닛테스트?)란 지난번에 TDD때 각 메서드들은 다 구분되어 서로를 알아보지 못하게 만들었다.
//   이것처럼 메서드들 각각 테스트한 결과(레이어마다)를 서로 못보게 잘라서 만드는것
//   Test에서 DB 사용후 원래대로 돌리는 작업까지 같이 해준다.
//   @ExtendWith룰 써줘야하는데 DataJpaTest가 가지고 있다. @Autowired 안써줘도 된다.
//   */
//
//@Import(JpaConfig.class)
///* 원래대로라면 JPA에서 모든 정보를 컨트롤해야하는데 JpaConfig의 경우 읽어오지 못한다.
//   이거는 시스템에서 만든게 아니라 우리가 별도로 만든 파일이기 때문이다. 그래서 별도로 import 한것
//   안하면 config안에 명시해놨던 JpaAuditing이 동작하지 않는다. */
//class Ex04_JpaRepositoryTest {
//    private final Ex04_ArticleRepository_기본테스트용 articleRepository;
//
//    private final Ex05_ArticleCommentRepository_기본테스트용 articleCommentRepository;
//
////    @Autowired
////    private ArticleRepository articleRepository;
//
//    /* 원래는 둘다 @Autowired가 붙어야 하는데, JUnit5 버전과 최신버전의 스프링 부트를 이용하면 Test에서 생성자 주입패턴 사용할수 있다.*/
//
//    /* 생성자 만들기 - 여기서는 다른 파일에서 매개변수로 보내주는걸 받는거라서 위에랑 상관없이 @Autowired를 붙여야함  */
//    public Ex04_JpaRepositoryTest(@Autowired Ex04_ArticleRepository_기본테스트용 articleRepository, @Autowired Ex05_ArticleCommentRepository_기본테스트용 articleCommentRepository) {
//        this.articleRepository = articleRepository;
//        this.articleCommentRepository = articleCommentRepository;
//    }
//
//    /* - 트랜잭션 시 사용하는 메서드(DB와 왔다갔다하는) - JPA에서는 쿼리문 안쓸거다 다음 함수로할거다
//         사용법: repository명.메소드()
//         1) findAll() - 모든 컬럼을 조회할때 사용, 페이징(pageable) 가능
//                        당연히 select 작업을 하지만 잠깐 사이에 테이블에 어떤 변화가 있었는지 알 수 없기 때문에
//                        select전에 먼저 최신 데이터를 잡기 위해서 update를 한다.
//                        동작순서 : update -> select
//         2) findById() - 한건에 대한 데이터 조회시 사용
//                         primary key로 레코드 한 건 조회
//                         ()안에 글번호를 넣어줘야 한다.
//         3) save() - 레코드 저장할 때 사용(insert, update)
//                   - saveAndFlush()
//         4) count() - 레코드 개수 뽑을 때 사용
//         5) delete() - 레코드 삭제
//         ----------------------------------------------------------
//         - 테스트용 데이터 가져오기
//           1) https://www.mockaroo.com/ 사이트 접속
//     */
//
//    // 테스트 이후에 실제 DB에 정보가 변하지않는다 test는 다시 rollback한다.
//    /** select 테스트 */
//    @DisplayName("셀렉트 테스트")
//    // 함수에 대한 별칭 달아주기
//    @Test
//    void selectTest() {
//        /** 셀렉트 할거니까 articleRepository 를 기준으로 테스트 할거임
//         *  maven방식: dao -> mapper로 정보 보내고 DB갔다와서 Controller까지 돌려보낼건데 dao에서 DTO를 list에 담아서
//         *  return을 시켜줬음
//         *  gradle방식은 dao, mapper가 없고 Jpa(findAll())가 그 역할을 한다.
//         *  db와 mapping을 해주기위한 framework
//         *  DTO(Data Transfer Object)
//         *
//         *  garbage Collector 동작방식
//         *  1. Garbage collector가 Stack의 모든 변수를 스켄하면서 각각 어떤 객체를 참조하고 있는지 찾아서 마킹
//         *  2. Reachable Objec(리스트 안에 객체, 스택 → 힙(리스트) →힙(객체))가 참조하고 있는 객체도 찾아서 마킹 (1, 2 번을 Mark라 부름)
//         * 3. 마킹되지 않은 객체를 Heap에서 제거 (Sweep이라 부름)
//         * 4. compact를 하는 GC라면, 단편화 된 메모리를 정리
//         */
//
//        List<Article> articles  = articleRepository.findAll();
//        // Jpa에서는 사용자가 selectAll같이 이름을 정해줄수없다 정해진 findAll() 메서드를 쓸거다
//        // 제네릭스의 역할 정해진 타입의 요소만 받을거야 다른거는 튕겨낼거야
//
//        /* assertJ를 이용해서 테스트 할거임
//           List에 담겨있는 Articles가 NotNull이고 사이즈가 ??개면 통과 */
//        assertThat(articles).isNotNull().hasSize(100);
//    }
//
//    /** insert 테스트 */
//    @Test
//    void insertTest() {
//    /* 기존의 article 갯수를 센 다음에 insert를 하고 기존거보다 현재것이 1차이가 나면
//       insert가 제대로 된것 */
//        // 기존 카운트 구하기
//        long prevCount = articleRepository.count();
//
//        // insert하기
//        Article article = Article.of("제목1", "내용1", "#해시태그1");
//        articleRepository.save(article);
//
//        // 기존것과 현재거랑 개수 차이 구하기
//        assertThat(articleRepository.count()).isEqualTo(prevCount+1);
//
//        /** !!주의 : 이상태로 테스트 돌리면 createAt 이거 못차는다고 에러남
//         *  이유 : jpaConfig파일에 auditing을 쓰겠다고 셋팅을 해놨는데,
//         *  해당 엔티티(Article.java) auditing을 쓴다고 명시를 안해놓은 상태라서
//         *  앤티티가서 클래스별로 @EntityListeners(AuditingEntityListener.class) 걸어주자
//         */
//    }
//
//    @Test
//    void updateTest() {
//        /* 기존의 데이터 하나 있어야 되고, 그걸 수정했을때를 관찰할거임.
//           1) 기존의 영속성 컨텍스트로부터 하나 엔티티 객체를 가져온다.(DB에서 한줄가져온다)
//           2) 업데이트로 해시태그를 바꾸기
//         */
//
//        /** 순서 -
//            1) 기존의 영속성 컨텍스트로부터 하나 엔티티 객체를 가져온다.(DB에서 한줄가져온다)
//             articleRepository - 기존의 영속성 컨텍스트로부터
//             findById(1L) > 하나의 앤티티 객체를 가져온다.
//             .orElseThrow() > 없으면 throw시켜서 일단 테스트가 끝나게하자
//         */
//        Article article = articleRepository.findById(1L).orElseThrow();
//
//        /** 순서2) 업데이트로 해시태그를 바꾸기
//         *  엔티티에 있는 setter를 이용해서 updateHashtag에 있는 문자열로 업데이트 하기
//         *  1. 변수 updateHashtag에 바꿀 문자열 저장
//         *  2. 엔티티(article)에 있는 setter를 이용해서 변수 updateHashtag에 있는 문자열을 넣고
//         *  (해시태그 바꿀거니까 setHashtag. 이름 어찌할지 모르겠으면 실제 엔티티 파일 가서 setter 만들어보기, 그이름 그대로 쓰면 됨)
//         *  3. 데이터베이스에 업데이트하기
//         *  */
//        String updateHashtag = "#abcd";
//        article.setHashtag(updateHashtag);
////        articleRepository.save(article); // rollback 할거니 실제 업데이트안하고 확인만한다 그래서 flush 사용하여 실제 업데이트 해본다 그러나 결국에는 테스트니 다시 rollback
//        Article savedArticle = articleRepository.saveAndFlush(article);
//        /* save로 놓고 테스트를 돌리면 콘솔(Run)탭에 update구문이 나오지 않고
//           select 구문만 나온다. 이유는 영속성 컨텍스트로부터 가져온 데이터를 그냥 save만 하고
//           아무거도 하지 않고 끝내버리면 어차피 롤백이 되니까 스프링부트는 다시 원래의 값으로
//           돌아갈 질거다. 그래서 그냥했다치고 update르 하지 않는다(코드의 유효성은 확인)
//           그래서 save를하고 flush를 해줘야한다.
//
//           - flush란(push같은거)
//             1. 변경점 감지
//             2. 수정된 Entity를 sql 저장소에 등록
//             3. sql저장소에 있는 쿼리를 DB에 전송
//         */
//
//        /** 순서3) 위에서 바꾼 savedArticle에 업데이트 된 hashtag 필드에 updateHashtag에
//         * 저장되어 있는 값("#abcd")이 있는지 확인해봐라 */
////        assertThat(savedArticle).hasFieldOrProperty();
//        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updateHashtag);
////        assertThat(savedArticle.getHashtag()).isEqualTo(updateHashtag);
//    }
//
//    @Test
//    void deleteTest() {
//        /** 기존의 데이터들이 있다고 치고, 그중에 값을 하나 꺼내고, 지워야한다.
//         *  1) 기존의 영속성 컨텍스트로부터 하나 엔티티를 객체로 가져온다. => findById
//         *  2) 지우면 DB에서 하나 사라지기 때문에 count를 구해놓고 > 레포지토리.count();
//         *  3) delete하고 (-1) > .delete();
//         *  4) 2번에서 구한 count와 지금 순간의 개수를 비교해서 1 차이나면 테스트 통과 > .isEqualTo()
//         */
//
//        /*
//           1) 기존의 영속성 컨텍스트로부터 하나 엔티티를 객체로 가져온다. => findById
//           - 순서
//             articleRepository > 기존의 영속성 컨텍스트로부터
//             findById(1L) > 하나의 엔티티 객체를 가져온다.
//             .orElseThrow() > 없으면 throw시켜서 일단 테스트가 끝나게 하자 */
//        Article article = articleRepository.findById(1L).orElseThrow();
//
//        /* 2) 지우면 DB에서 하나 사라지기 때문에 count를 구해놓고
//              게시글(articleRepository) 뿐만아니라, 연관된 댓글(articleCommentRepository)
//              까지 삭제할거라서 두개의 개수를 다 알아내기 */
//        long prevArticleCount = articleRepository.count();
//        long prevArticleCommnetCount = articleCommentRepository.count(); // 데이터베이스에 있는 모든 댓글의 수
//        int deletedCommentSize = article.getArticleComments().size(); // 해당 게시글에 딸려있는 모든 댓글의 수
//        // 나중에 "모든 댓글의 수 - 게시글에 딸려있는 댓글 수" 하면 몇개 지워졌는지 알 수 있다.
//        System.out.println("deletedCommentSize = " + deletedCommentSize);
//
//        /* 3) delete하고(전체 게시글 개수 -1됨) */
//        articleRepository.delete(article);
//
//        /* 테스트 통과 조건 - 2번에서 구한거랑 여기서 구하는 개수가 1차이 나는 경우 */
//        assertThat(articleRepository.count()).isEqualTo(prevArticleCount - 1);
//        assertThat(articleCommentRepository.count()).isEqualTo(prevArticleCommnetCount - deletedCommentSize);
//
//
//    }
//
//}