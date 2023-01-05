package com.bitstudy.app.repository;

import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface Ex07_5_ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>,
        QuerydslBinderCustomizer<QArticleComment> {

    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        /** 1. 바인딩(오버라이트 ctrl+O)
         *  현재 QuerydslPredicateExecutor 때문에 Article에 있는 모든 필드에 대한 검색이 열려있는 상태이다. 근데 우리가 원하는 건 선택적인 필드(제목, 본문, id, 글쓴이, 해시태그)만 검색에 사용되도록 하고싶다. 그래서 선택적으로 검색을 하기 위해 bindings.excludeUnlistedProperties를 쓴다.
         *  excludeUnlistedProperties는 리스팅을 하지 않은 프로퍼티는 검색에 포함할지 말지 결정할 수 있는 메서드이다. true면 검색에서 제외, false는 모든 프로퍼티 열어주는거
         * */
        bindings.excludeUnlistedProperties(true); // 검색 필요한것들을 선택적으로 하기위해

        /** 2. 검색용(원하는) 필드를 지정(추가) 하는 부분
         *     including을 이용해서 title, content, createBy, createAt, hashtag 검색 가능하게 만들거임.
         *     id는 인증기능 달아서 유저 정보를 알아올 수 있을때 할거임.
         *     including 사용법: 'root.필드명'
         * */
        bindings.including(root.content, root.createdAt, root.createdBy);

        /** 3. 정확한 검색만 되었었는데 'or검색' 가능하게 바꾸기 */
        // 네이버 같은 경우는 or검색이다 띄어쓰기 시 or로 인식, 바인딩은 한줄에 하나씩밖에 못건다.
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); // like '%#{문자열}%'
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); // 이건 날짜니까 DateTimeExpression으로하고, eq는 equals의 의미, 날짜필드는 정확한 검색만 되도록 설정, 근데 이렇게하면 시분초가 다 0으로 인식됨, 이부분은 별도로 시간처리할때 건드릴 거임
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }

    /** 1) 빌드(ctrl + F9, ctrl+shift+F9)
     *  2) HAL 가서 체크하기
     *     ex) http://localhost:8080/api/articleComments?createBy=Klaus
     *     ex) http://localhost:8080/api/articles?createBy=Klaus
     *
     */
}
