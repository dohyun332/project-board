package com.bitstudy.app.service;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


/** 서비스 비즈니스 로직은 슬라이스 테스트 기능 사용 안하고 만들어볼거임
 *  스프링부트 어플리케이션 컨텍스트가 뜨는데 걸리는 시간을 없애려고한다.
 *  디펜던시가 추가되야 하는 부분에는 Mocking을 하는 방식으로 한다.
 *  그래서 많이 사용하는 라이브러리가 mockito라는게 있다.(스프링 테스트 패키지에 내장되어 있음.)
 *
 *  @ExtendWith(MockitoExtension.class) 쓰면된다.
 * */
//@WebMvcTest controller가 아니라 쓸수없다.
@ExtendWith(MockitoExtension.class)
class Ex13_2_ArticleServiceTest_상세페이징 {
    /* Mock을 주입하는 거에다가 @InjectMocks를 달아줘야한다. 그외의 것들한테는 @Mock 달아준다. @ExtendWithd에 사용하는 애너테이션*/
    @InjectMocks
    private ArticleService sut; // sut - system under test. 테스트 짤때 사용하는 이름 중 하나, 이건 테스트 대상이다 라는 뜻

    @Mock
    private ArticleRepository articleRepository;

    /*   테스트할 기능들 정리
         1. 검색
         2. 각 게시글 선택하면 해당 상세 페이지로 이동
         3. 페이지네이션 */

    /** 1. 검색 */
    @DisplayName("검색어 없이 게시글 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void withoutKeywordReturnArticlesAll() {
        // Given - 페이지 기능 넣기
        Pageable pageable = Pageable.ofSize(20); // 한페이지에 몇개 가져올건지 결정
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());
        /** Pageable - org.springframework.data.domain
         *  given - org.mockito.BDDMockito */
        // 빈페이지가 넘어올거야 알고있어라

        // When - 입력 없이(null) 실제 테스트 돌리는 부분
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
        // should() 1회 불렀어?, findAll 불렀어??
    }

    @DisplayName("검색어 이용해서 게시글 검색하면, 게시글 리스트를 반환")
    @Test
    void withKeywordReturnArticlesAll() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn((Page.empty()));

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    /** 2. 게시글 페이지 이동 */
    @DisplayName("게시글 선택하면, 게시글(하나) 반환")
    @Test
    void selectedArticleReturnArticleOne() {
        // Given
        Article article = createArticle();
        Long articleId = 1L;
        given(articleRepository.findById(1L)).willReturn((Optional.of(article)));
        /* Optional, article 있을수도 없을수도 있어(없어도 돼) */

        // When
        ArticleWithCommentsDto dto = sut.getArticle(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }


    /** 3. 게시글 생성 */
    @DisplayName("게시글 정보 입력하면, 게시글(하나) 생성한다.")
    @Test
    void givenGetArticleInfoWhenCreateArticleOne() {
        // Given
            ArticleDto dto = createArticleDto();
            given(articleRepository.save(any(Article.class))).willReturn(createArticle());
            // any는 어떤 게시글이 들어오던 상관없다.
        // When
            sut.saveArticle(dto);
        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    /** 4. 게시글 수정 */
    @DisplayName("게시글 수정 정보 입력하면, 게시글(하나) 수정한다.")
    @Test
    void givenModifiedArticleInfoWhenUpdateArticleOne() {
        // Given
        ArticleDto dto = createArticleDto("title", "content", "#java");
        Article article = createArticle();
        given(articleRepository.getReferenceById(dto.id()))
                // 레코드에 들어있는 id값 가져오려면 getid()가 아니라 id()
                // d대신 이걸 불러다 쓸때는 일반필드처럼 가져다 쓰면된다.
                .willReturn(article);
        // save써도 되긴하는데 save는 실제 insert문을 날리는데 insert하기전에 jpa에서 select를 해서 있는지 확인한번한다. .getReferenceById()는 기존에 무언가 있는 것에만 사용할 수 있다. .getReferenceById()는 업데이트해야하는 해당 데이터만 참조하고 있다.

        // When
        sut.updateArticle(dto);

        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    /** 5. 게시글 삭제 */
    @DisplayName("게시글 ID를 입력하면, 게시글(하나) 삭제한다.")
    @Test
    void givenArticleIdWhenDeleteArticleOne() {
        // Given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);
        // 초

        // When
        sut.deleteArticle(articleId);

        // Then
        then(articleRepository).should().deleteById(articleId);
    }
/* 새로 추가 */
    @DisplayName("게시글 수 조회하면, 게시글 수 반환")
    @Test
    void givenNothing_thenReturnArticleCount() {
        // Given
        long expected = 0;
        given(articleRepository.count()).willReturn(expected);
        // When
        long actual = sut.getArticleCount();

        // Then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "bitstudy",
                "password",
                "bitstudy@email.com",
                "bitstudy",
                null
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(
                1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
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
                "memomemo",
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
                );
    }

}