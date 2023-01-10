package com.bitstudy.app.service;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.ArticleComment;
import com.bitstudy.app.dto.ArticleCommentDto;
import com.bitstudy.app.repository.ArticleCommentRepository;
import com.bitstudy.app.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
// final인 놈들을 만들어줌
@Transactional
public class ArticleCommentService {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    /* 댓글 리스트 조회*/
    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComment(long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId)
                .stream()
                .map(ArticleCommentDto::from)
                .toList();
    }

    /* 댓글 저장*/
    @Transactional(readOnly = true)
    public void saveArticleComment(ArticleCommentDto dto) {
        try {
            articleCommentRepository.save(dto.toEntity
                    (articleRepository.getReferenceById(dto.articleId())));
        } catch(EntityNotFoundException e) {
            log.warn("댓글 저장 실패");
        }
    }

    /* 댓글 수정 */
    @Transactional(readOnly = true)
    public void updateArticleComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            // getReferenceById(): save의 select후 update나 insert 하는 과정에서 select 과정 제외

            if(dto.content() != null) {
                // 댓글을 null로하고 입력 시 유효성 검사
                articleComment.setContent(dto.content());
            }
        } catch(EntityNotFoundException e) {
            log.warn("댓글 수정 실패");
        }
    }

    /* 댓글 삭제 */
    @Transactional(readOnly = true)
    public void deleteArticleComment(Long articleCommentId) {
        articleCommentRepository.deleteById(articleCommentId);
    }

}
