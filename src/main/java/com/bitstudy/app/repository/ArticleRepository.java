package com.bitstudy.app.repository;

import com.bitstudy.app.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/** TDD를 위해서 임시로 만들어 놓은 저장소(이것으로 DB 접근), DAO의 역할
 *
 * - TDD 만드는 방법
 *   1) 클래스 이름에서 우클릭 > Go to > Test(ctrl + shift + T)
 *   2) JUnit5 버전인지 확인
 *   3) Test에 경로가 같게 생성되는지 확인 후 ok
 *   4) 이름 ArticleRepositoryTest를 JpaRepositoryTest로 변경
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
