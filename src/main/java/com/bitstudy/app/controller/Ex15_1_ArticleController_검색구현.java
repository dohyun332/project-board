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

/* 할일: 제목, 본문, 이름 순으로 검색기능이 동작하도록 한다. */

@RequiredArgsConstructor
@Controller
@RequestMapping("/articles15_1")
public class Ex15_1_ArticleController_검색구현 {

    private final ArticleService articleService;
    private final PaginationService paginationService;


    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map) {

        Page<ArticleResponse> articles = articleService.searchArticles(searchType,searchValue,pageable).map(ArticleResponse::from);

        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
/* 추가 */
        map.addAttribute("searchTypes", searchType.values());
        /* values() enum의 값을 배열로 가져오는 함수 */

        return "articles/index";
    }

    /* 게시글 상세 페이지*/
    @GetMapping("/{articleId}")
    public String article(ModelMap map, @PathVariable Long articleId) {
        ArticleWithCommentsResponse article =
                ArticleWithCommentsResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponse());
        map.addAttribute("totalCount", articleService.getArticleCount());

        return "articles/detail";
    }
}
