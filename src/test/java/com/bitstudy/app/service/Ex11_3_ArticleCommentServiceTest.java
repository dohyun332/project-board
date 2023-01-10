package com.bitstudy.app.service;

/* 할일 댓글의 CRUD관련된 테스트만 만들기 */

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.domain.UserAccount;
import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.dto.UserAccountDto;
import com.bitstudy.app.repository.ArticleCommentRepository;
import com.bitstudy.app.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class Ex11_3_ArticleCommentServiceTest {

    @InjectMocks private ArticleCommentService sut; // 테스트 대상
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCommentRepository articleCommentRepository;

    /* 댓글 리스트 조회 */
    @DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트 모두 반환")
    @Test
    void givenSearchById_thenReturnCommentsAll() {
        // BDD Mockito
        // Given
        ArticleComment expected = createArticleComment("content");
        /** 1L번 게시글 기준으로 모든 댓글들 다 리턴해오기 */
        Long articleId = 1L;
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of((expected)));

        // When
        List<ArticleCommentDto> actual = sut.searchArticleComment(articleId);

        // Then
        assertThat(actual)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("content", expected.getContent());
                // 첫번째 항목
    }

    /* 댓글 저장 */
    @DisplayName("댓글정보를 입력하면 댓글 저장")
    @Test
    void givenCommentInfo_thenSaveComment() {
        // BDD Mockito
        // Given
        ArticleComment expected = createArticleComment("content");
        /** 1L번 게시글 기준으로 모든 댓글들 다 리턴해오기 */
        Long articleId = 1L;
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of((expected)));

        // When
        List<ArticleCommentDto> actual = sut.searchArticleComment(articleId);

        // Then
        assertThat(actual)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("content", expected.getContent());
        // 첫번째 항목
    }

    /* 댓글 수정 */
    @DisplayName("댓글정보를 입력하면 댓글 수정")
    @Test
    void givenCommentInfo_thenUpdateComment() {
        // Given
        String oldContent = "content";
        String updateContent = "댓글";
        ArticleComment articleComment = createArticleComment(oldContent);/** 새로운 테스트용 댓글 생성 */
        ArticleCommentDto dto = createArticleCommentDto(updateContent);
        given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);

        // When : 실행하는 부분
        sut.updateArticleComment(dto);

        // Then
        assertThat(articleComment.getContent())
                .isNotEqualTo(oldContent) /* oldContent랑 다르고 */
                .isEqualTo(updateContent); /* updateContent랑 같으면 테스트 통과*/
        // 실제 테스트 부분은 여기이다. then은 안전장치를 추가한거다 과정을 확인한것
        // assertThat은 결과를 확인한것
        then(articleCommentRepository).should().getReferenceById(dto.articleId());
//        then(articleCommentRepository).shouldHaveNoInteractions();
        /** .shouldHaveNoInteractions(): 특정 시점이후로 한번 인터렉션 있는지 체크 */
    }

    /* 댓글 삭제 : 실제 데이터가 있는거는 아니지만 동작이 되는지?*/
    @DisplayName("댓글id를 입력하면 댓글 삭제")
    @Test
    void givenCommentId_thenDeleteComment() {
        // Given
        Long articleCommentId = 1L;
        willDoNothing().given(articleCommentRepository).deleteById(articleCommentId);
        // return 타입 void일때 willDoNothing()를 사용

        // When : 실행하는 부분
        sut.deleteArticleComment(articleCommentId);

        // Then
        then(articleCommentRepository).should().deleteById(articleCommentId);
    }
    /* @injectMock, @Mock 쓰는 이유
       DB가 크면 테스트가 느리고 덩치가 클것이다.
       해당 애너테이션 쓰면 가짜 데이터를 만든다.

       만든 @Mock들을 @injectMock에 넣어서 테스트 하겠다.
    * */

    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                content,
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
                "memo메모",
                LocalDateTime.now(),
                "bitstudy",
                LocalDateTime.now(),
                "bitstudy"
                );
    }


    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
                Article.of(
                        createUserAccount(),
                        "title",
                        "content",
                        "hashtag"
                ),
                createUserAccount(),
                            content
        );
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

}
