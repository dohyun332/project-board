package com.bitstudy.app.repository;

import com.bitstudy.app.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/** TDD를 위해서 임시로 만들어 놓은 저장소(이것으로 DB 접근), DAO의 역할
 *
 * - TDD 만드는 방법
 *   1) 클래스 이름에서 우클릭 > Go to > Test(ctrl + shift + T)
 *   2) JUnit5 버전인지 확인
 *   3) Test에 경로가 같게 생성되는지 확인 후 ok
 *   4) 이름 ArticleRepositoryTest를 JpaRepositoryTest로 변경
 */

/**
 *
 *  HAL 확인해보기 서비스 실행 하고, 브라우저에서 localhost:8080/api 넣기
 *
 *  테스트 만들기 (test > controller > Ex07_3_DataRestTest_실패하는테스트.java)
 *
 * */

@RepositoryRestResource
public interface Ex07_2_ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
}
