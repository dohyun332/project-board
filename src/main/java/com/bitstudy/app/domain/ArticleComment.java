package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createAt"),
        @Index(columnList = "createBy")
})
@Entity /* 테이블과의 매핑한다는 뜻,
           JPA가 관리한다.
           PK가 알아볼수 있게 필드들 중 하나에 @Id 어노테이션 달아줘야 한다. */
@Getter
@ToString

public class ArticleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private Article article;
//@Setter
//@Column(nullable = false)
//private Long articleId;
    /** 연관관계 매핑
     *  연관관계 없이 만들면 private Long articleId; 이런식으로 (관계형 데이터베이스 스타일) 하면된다.
     *  그런데 우리는 Ariticle과 ArticleComment가 관계를 맺고 있는 것을 객체 지향적으로 표현하려고 이렇게 쓸거다.
     *  그러기 위해서 필요한건 단방향(1:N)이라는 뜻의 @ManyToOne 에너테이션을 써주고,
     *  필수값이라는 의미로 (optional = false)
     *  '댓글은 여러개:게시글 1개' 이기때문에 단방향 방식을 썼다.
     */

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문

    // 메타데이터
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createAt; // 생성일시

    @CreatedBy
    @Column(nullable = false, length = 100)
    private String createBy; // 생성자

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일시

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; // 수정자

}
