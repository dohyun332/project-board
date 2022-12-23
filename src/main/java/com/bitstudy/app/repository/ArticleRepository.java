package com.bitstudy.app.repository;

import com.bitstudy.app.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;

/** TDD를 위해서 임시로 만들어 놓은 저장소(이것으로 DB 접근), DAO의 역할
 *
 * - TDD 만드는 방법
 *   1) 클래스 이름에서 우클릭 > Go to > Test(ctrl + shift + T)
 *   2) JUnit5 버전인지 확인
 *   3) Test에 경로가 같게 생성되는지 확인 후 ok
 *   4) 이름 ArticleRepositoryTest를 JpaRepositoryTest로 변경
 */

/** 할일 - 클래스 레벨에 @RepositoryRestResource 넣어서 해당 클래스를 spring rest data 사용할 준비 시켜놓기
 *
 */

/* HAL안될때
   1. ctrl+shift+R로 새로고침(캐시없애고 새로고침)
   2. @RepositoryRestController 넣지 않게 조심
   3. dependency rest, rest HAL 다 있는지 확인
 */
@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
