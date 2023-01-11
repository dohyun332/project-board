package com.bitstudy.app.controller;


import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.response.ArticleResponse;
import com.bitstudy.app.dto.response.ArticleWithCommentsResponse;
import com.bitstudy.app.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.RequestMapping;


/** 뷰 엔드포인트 관련 컨트롤러
 *
 * 엑셀 api에 보면 정의해놓은 view부분에 url들이 있다. 그거 보면서 하면됨
 * /articles	                GET	게시판 페이지
 * /articles/{article-id}	    GET	게시글 페이지
 * /articles/serach	            GET	게시판 검색 전용 페이지
 * /articles/serach-hashtag	    GET	게시판 해시태그 검색 전용 페이지
 *
 *
 * Thymeleaf: 뷰 파일은 HTML로 작업될건데, 타임리프를 설치함으로서 스프링은 이제 html파일을 마크업으로 보지 않고, 타임리프 템플릿 파일로 인식한다.
 * 그래서 이 HTML파일들을 아무데서나 작성할 수 없고, resources > templates 폴더 안에만 작성 가능하다.
 * 그외에 css, img 그리고 js들은 resources > static 폴더 안에 작성가능
 * */

@RequiredArgsConstructor // 필수 필드에 대한 생성자 자동생성
//@RequiredArgsConstructor 초기화 되지않은 final 필드 또는 @Nonnull이 붙은 필드에 대해 생성자 생성 lombok애너테이션
@Controller
@RequestMapping("/articles08_2") // 모든 경로들은 /articles로 시작하니까 클래스 레벨에 1차로 @RequestMapping("/articles") 걸어놨음
public class Ex08_02_ArticleController {

    private final ArticleService articleService;
    /* @RequiredArgsConstructor로 만들어진 생성자(여기서는 articleService)를 얘가
    * 읽어서 정보의 전달을 할 수 있게한다. */

    /* BDD하러 가기*/
    /** 할일 나타낼때 주석임 마우스 올리면 해당 설명 나온다. */
    /* 게시판 리스트 보여주기 */
    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            /** 해설:
             *    @RequestParam: 검색어를 받아야한다. @RequestParam을 이용해서        getParameter를 불러올거고, 반드시 있지 않아도 된다.
             *    없으면 게시글 전체 조회하면 되니까 SearchType, searchValue에
             *   (required = false) 달아서 null 들어올수 있게 걸어놓을수 있다.
             *    @PageableDefault: 페이징 기본설정(한페이지에 10개, 작성일순, 내     림차순)
             * */
            ModelMap map) {


        /** ModelMap: 테스트파일에서 .andExpect(model().attributeExists("articles"));를 이용해서 articles라는 키 값으로
         *  데이터를 넣어주기로 했으니까 필요함
         *  Model과 ModelMap은 같은거임. 차이점은 Model은 인터페이스, ModelMap은 클래스(구현체)
         *  */
        //map.addAttribute("articles", List.of());
        // 키: articles, 값: 그냥 list

        map.addAttribute("articles", articleService.searchArticles(searchType,searchValue,pageable).map(ArticleResponse::from));
        // 진짜로 정보를 넣어줘야하니까 ArticleService.java에 만들어 놓은 searchArticles()메서드에 값을 넣어주면 됨
        // 그런데 searchArticles()의 반환 타입은 DTO인데 dto는 모든 엔티티의데이터를 다 다루고 있어서 그걸 한번 더 가공해서 필요한 것들만 쓸거다. 그래서 게시글 내용만 가지고 있는 ArticleResponse를 사용한다.
        return "articles/index";
    }

    /* 게시글 상세 페이지*/
    @GetMapping("/{articleId}")
    public String article(ModelMap map, @PathVariable Long articleId) {
        ArticleWithCommentsResponse article =
                ArticleWithCommentsResponse.from(articleService.getArticle(articleId));
        System.out.println("article99 = " + article);

        map.addAttribute("article", article);
//        map.addAttribute("article", "aaa");
        // 지금당장은 받아오지 않기 때문에 null이라고 넣었지만, 테스트할때는 뭐라도 문자열을 넣어서 모델에 담기도록한다.
        map.addAttribute("articleComments", article.articleCommentsResponse());
        // article.articleCommentsResponse() 해설: 현재 article에  article.articleCommentsResponse의 정보가 담겨있으니까 article안에 있는 articleComments를 꺼내면 된다.
//        map.addAttribute("articleComments", List.of());

        return "articles/detail";
    }


}
