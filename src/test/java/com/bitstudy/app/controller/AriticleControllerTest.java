package com.bitstudy.app.controller;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

/** 하기전에 알아둘것: 이테스트 코드를 작성하고 돌리면 결과적으로 404에러난다.
 *  이유는 아직 ArticleController에 작성된 내용이 없고, dao같은 것들도 없기 때문이다.
 *
 *  우선 작성하고 실제코드(ArticleController)와 연결되는지 확인.
 *
 *  슬라이스 테스트 방식으로 테스트 할거임
 * */

/* autoConfiguration을 가져올 필요가 없기 때문에 슬라이스 테스트 사용가능 */
//@WebMvcTest // 이렇게만 쓰면 모든 컨트롤러들 다 읽어 들인다. 지금은 컨트롤러 디렉토리에 파일이 하나밖에 없어서 상관없지만 많아지면 모든 컨트롤러들을 bean으로 읽어오기 때문에 아래처럼 필요한 클래스만 넣어주면 됨
@WebMvcTest(ArticleController.class)
@DisplayName("view 컨트롤러 - 게시글")
class ArticleControllerTest {
    private final MockMvc mvc;

    public ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }
    /**테스트는 엑셀 api에 있는 순서대로 만들거임
     * 1) 게시판 리스트 페이지
     * 2) 게시판 상세 페이지
     * 3) 게시판 검색전용
     * 4) 해시태그 검색 전용페이지
     *
     *  엑셀 api에 보면 정의해놓은 view부분에 url들이 있다. 그거 보면서 하면됨
     *  /articles	                GET	게시판 페이지
     *  /articles/{article-id}	    GET	게시글 페이지
     *  /articles/serach	            GET	게시판 검색 전용 페이지
     *  /articles/serach-hashtag	    GET	게시판 해시태그 검색 전용 페이지
     */


}