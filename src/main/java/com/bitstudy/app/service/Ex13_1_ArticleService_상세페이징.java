package com.bitstudy.app.service;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.type.SearchType;
import com.bitstudy.app.dto.ArticleDto;
import com.bitstudy.app.dto.ArticleWithCommentsDto;
import com.bitstudy.app.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


/** 기능 관련 로직을 다여기다가 짤거임 */

@Slf4j // 로그를 남길때 사용하는 롬복애너테이션
@Service
@RequiredArgsConstructor // 필수 필드에 대한 생성자를 자동으로 만들어주는 롬복 애너테이션
@Transactional // 이 클래스 동작하다가 하나라도 잘못되면 다시 롤백 시켜라 뜻
public class Ex13_1_ArticleService_상세페이징 {
    private final ArticleRepository articleRepository;
    // 아티클 관련 서비스이기때문에 ArticleRepository 필요

    // 검색용
    @Transactional(readOnly = true) // 트랜잭션을 읽기 전용 모드로 설정 실수로 커밋해도 flush가 되지 않아서
    // 엔티티가 등록, 수정, 삭제가 되지 않는다.

    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        // JPA부분이다. pageable(limit도 걸어준다.) 타입은 타이틀이고,
//        return Page.empty();

        /** page: 전체 데이터 조회(데이터를 다가지고 있다.)
         *        getTotalElements(): 데이터 개수 뽑기
         *        getTotalPages(): 별도의 size를 줘서 총 페이지 개수
         *        getNumber(): 가져온 페이지의 번호를 뽑아준다.
         *
         *  Pageable: 페이징 기능
         *            JPA에서 DB 쿼리 날릴때 limit를 날려서 개수제한한 데이터 가져옴
         */

        /** ArticleServiceTest의 withoutKeywordReturnArticlesAll()
         * 부분 관련 실제 구현
         * When 부분에서 searchArticles 메서드를 이용해서 값들을 전달한다.
         * 사실 검색 관련된 부분이기 때문에 검색어인 searchKeyword를 이용해서 코드를 짤거임
         * */

        /* 검색어 없는 경우 */
        if(searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(x -> ArticleDto.from(x));
            // 내용물을 형변환해서(map 메소드임 일반 map과는 다름)
            // 정보를 사용하기 위해 DTO로 변환
            /* Page클래스 맨 아래에 map()이 있다. 이건 Page안의 내용물을 형변환해서 다시 페이지로 새로 만들어 준다. map()이 article을 articleDto로 바꿔서 리턴해줌
            * */
        }
        
        /* 검색어 있는 경우 */
        /** searchType은 enum으로 되어 있음.
         *  enum을 주제로 각각의 쿼리를 만들거임.
         *  ex) Title 검색 시 제목 검색 쿼리, ID검색 시 아이디 검색 쿼리 반환
         * */

        return switch (searchType) {
            /** 원래 switch case 방식
             * cast TITLE:
             *      코드
             *      break;
             * 이런식이었는데 '->' 키워드를 이용해서 알아서 리턴까지 할수있게 발전
             */
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagContaining(searchKeyword, pageable).map(ArticleDto::from);
        };
    }

    /** 게시글 하나 호출 */
    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from) // 게시글 하나 잡고, Dto로 변환해줘야 해서 map() 사용한다.
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다. - articleId: " + articleId)); // throws Exception과 같다 ;
    }

    /** 게시글 생성 */
    public void saveArticle(ArticleDto dto) {
        /* select 구문때 처럼 DB에서 가져올 정보가 없기 때문에 그냥 void라 return이 없다. */
        articleRepository.save(dto.toEntity());
        // toEntity를 이용해서 매개변수로 받은 dto(단순히 데이터를 가지고 있기만 한, 누가 어떤 정보인지 모르는 상태) 정보로부터 엔티티를 하나 만들어서 세이브하는 코드
        // jpa는 알고있다. DB 접근할때는 entity로 바꿔서 접근
    }

    /** 게시글 수정 */
    public void updateArticle(ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            /* 매개변수 dto로 받은 title, content는 not null 필드기 때문에 if로 감싸서 null이 아닌 경우에만 동작하도록함 */
            if(dto.title() != null) {
                article.setTitle(dto.title());
            }
            if(dto.content() != null) {
                article.setTitle(dto.content());
            }
            article.setHashtag(dto.hashtag());
            // null 가능 필드라서 if문으로 검사할 필요 없다.
        } catch (EntityNotFoundException e) {
            log.warn("게시판 업데이트 실패. 게시글을 찾을수 없습니다. dto:" + dto);
        }
    }

    /** 게시글 삭제 */
    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    /* 게시글 개수 구하기 - 마지막 글일때는 다음 버튼 비활성화 시켜야 하기때문 */
    public long getArticleCount() {
        return articleRepository.count();
    }
}
