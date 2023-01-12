package com.bitstudy.app.controller;


import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.response.ArticleResponse;
import com.bitstudy.app.dto.response.ArticleWithCommentsResponse;
import com.bitstudy.app.service.ArticleService;
import com.bitstudy.app.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RequiredArgsConstructor
@Controller
@RequestMapping("/articles14_1")
public class Ex14_1_ArticleController_게시판리스트정렬 {

    private final ArticleService articleService;
    private final PaginationService paginationService;


    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map) {

//        map.addAttribute("articles", articleService.searchArticles(searchType,searchValue,pageable).map(ArticleResponse::from));

        Page<ArticleResponse> articles = articleService.searchArticles(searchType,searchValue,pageable).map(ArticleResponse::from);

        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);

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

/* 새로 생성 */
        map.addAttribute("totalCount", articleService.getArticleCount());

        return "articles/detail";
    }


}
