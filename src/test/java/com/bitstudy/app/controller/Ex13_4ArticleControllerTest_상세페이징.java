package com.bitstudy.app.controller;

import com.bitstudy.app.config.SecurityConfig;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.service.ArticleService;
import com.bitstudy.app.service.PaginationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/* 지금 상태로 테스트 돌리면 콘솔창에 401이 뜬다.
  (401: 파일을 찾긴 찾았는데 인증을 못받아서 못들어간다 라는 뜻)
  이유는 기본 웹 시큐리티를 불러와서 그런거다.
  config > SecurityConfig를 읽어오게 하면 된다.
  방법 :@Import(SecurityConfig.class)를 클래스 레벨에 넣어서 현재 이 테스트코드에서도 읽히게 해줌
*/

@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)
@DisplayName("view 컨트롤러 - 게시글")
class Ex13_4ArticleControllerTest_상세페이징 {
    private final MockMvc mvc;
    @MockBean
    private ArticleService articleService;

    @MockBean
    private PaginationService paginationService;
    /** @MockBean: 테스트 시 테스트에 필요한 객체를 bean으로 등록시켜서 기존 객체 대신 사용 할 수 있게 만들어줌, 실세 service와 연결끊고 Mock으로 가짜로 만듦
     *
     * ArticleController에 있는 private final
     * ArticleService articleService; 부분의 articleService
     * 를 배제하기 위해서 @MockBean 사용함. 이유는 MockMvc가 입출력 관련된 것들만 보게
     * 하기 위해서 진짜 서비스 로직을 끊어주기 위해 @MockBean사용
     * */

    public Ex13_4ArticleControllerTest_상세페이징(@Autowired MockMvc mvc) {
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

    /* 1) 게시판(리스트) 페이지*/
    @Test
    @DisplayName("[view][GET] 게시글 리스트(게시판) 페이지 - 정상호출")
    public void articlesAll() throws Exception{
        /** searchKeyword 없을때 검색어 없이 , eq는 equal이라는 뜻
         *  얘네들은 null이라는 값만 들어가야해
         * */
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());
        // 하나라도 matcher를 쓰면 모든 인자 matcher써야함


        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0,1,2,3,4));
        //Page.empty() 뭐가 넘어오든 페이지만 넘어오면된다.


        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"));

        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));

        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }
    /* 2) 게시판(상세) 페이지*/
    @Test
    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상호출")
    public void articlesOne() throws Exception{
        Long articleId = 1L;

/* 새거 */
        long totalCount = 1L;

        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());

/* 새거 */
        given(articleService.getArticleCount()).willReturn(totalCount);

        mvc.perform(get("/articles/1")) // 테스트니까 그냥 1번글 가져와라 할거임
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"))
                // 상세페이지에는 댓글들도 같이 오니까 모델 어트리뷰트에 articleComments 키가 있는지 확인
/* 새거 */
                .andExpect(model().attributeExists("totalCount"));

        then(articleService).should().getArticle(articleId);
/* 새거 */
        then(articleService).should().getArticleCount();
        // then은 service쪽 확인, assertThat은 cotroller쪽 확인한다.
    }

//    /* 3) 게시판 검색 전용*/
//    @Disabled("구현중")
//    @Test
//    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상호출")
//    public void articlesSearch() throws Exception {
//        Long articleId = 1L;
//        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());
//
//        mvc.perform(get("/articles/search"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
//                .andExpect(view().name("articles/search"));
//    }

//    /* 4) 해시태그 검색 전용 페이지*/
//    @Disabled("구현중")
//    @Test
//    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상호출")
//    public void articlesSearchHashtag() throws Exception {
//        mvc.perform(get("/articles/search-hashtag"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
//                .andExpect(view().name("articles/search-hashtag"));
//    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "bitstudy",
                "password",
                "bitstudy@email.com",
                "bitstudy",
                "memo memo",
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
        );
    }
}