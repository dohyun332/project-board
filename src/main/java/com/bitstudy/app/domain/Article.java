package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** Article과 ArticleComment에 있는 공통필드(메타데이터, ID제외)들을 별도로 빼서 관리할거임.
 *  이유는 앞으로 Article과 ArticleComment처럼 fk같은거로 엮여 있는 테이블들을 만들경우, 모든 domain안에
 *  있는 파일들에 많은 중복코드들이 들어가게된다. 그래서 별도의 파일에 공통되는 것들을 다 모아놓고 사용하기
 *
 *  참고: 공통필드를 빼는 건 팀마다 다르다.
 *       중복코드를 싫어해서 각 파일마다 다 두는 사람들이 있고,(유지보수에 유리)
 *       중복코드를 괜찮아 해서 각파일에 그냥 두는 사람도 있다.
 *       (각 파일에 모든 정보가 다 있어 변경시 유연하게 코드 작업을 할 수 있다.)
 *
 *  추출은 2가지 방법으로 할 수 있다.
 *  1) @Embedded - 공통되는 필드들을 하나의 클래스로 만들어서 @Embedded 있는 곳에서 치환하는 방식
 *  2) @MappedSuperClass - (요즘실무에서 사용하는 방식)
 *          @MappedSuperClass 어노테이션이 붙은 곳에서 사용
 *  * 둘의차이: 사실 둘이 비슷하지만 @Embedded방식을 하게 되면 필드가 하나 추가된다.
 *             영속성컨텍스트를 통해서 데이터를 넘겨 받아서 어플리케이션으로 열었을때에는
 *             어차피 AuditionField랑 똑같이 보인다.
 *             (중간에 한단계가 더 있다는 뜻)
 *  @MappedSuperClass는 표준 JPA에서 제공해주는 클래스, 중간단계 따로없이 바로동작
 */
//@EntityListeners(AuditingEntityListener.class)
/* 이거없으면 테스트할때 createdAt 때문에 에러남(Ex04 관련) 메타데이터 추출했기에 추출한곳으로 이동*/
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
}) // 용량때문에 성능이 느려질수 있으니 잘사용하는것만, 검색 속도 증가시키기위한 색인
@Entity /* 1) 롬복을 이용해서 클래스를 앤티티로 변경 @Entity가 붙은 클래스는 JPA가 관라하게된다.
              그래서 기본키(PK)가 뭔지 알려줘야한다. 그게 @Id 에너테이션이다. */
@Getter // 롬복에 쓰면 알아서 모든 필드의 getter들이 생성
@ToString(callSuper = true) // 저 아래 상위요소인 UserAccount의 toString까지 출력할 수 있도록 callSuper를 넣음
public class Article extends AuditingFields {
    // 인터페이스의 abstract는 public default밖에 못쓰니 private에 적합x

    @Id // 전체 필드 중에서 이것이 pk라고 지정, @Id가 없으면 @Entity 에러 발생
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 새로추가 */
    @Setter
    @ManyToOne(optional = false) // 단방향
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false)
    private String title; // 제목, not null

    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 본문, varchar안써주면 255먹음

    @Setter
    private String hashtag; // 해시태그

    /** 양방향 바인딩 */
//    @OrderBy("id") // 양방향 바인딩을 할건데 정렬 기준을 id로 하겠다는 뜻
    @OrderBy("createdAt desc") // 댓글리스트를 최근 시간거로 정렬되도록 바꿈
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    /* embedded 방식
    class Tmp {
        @CreatedDate @Column(nullable = false) private LocalDateTime createdAt; // 생성일시
        @CreatedBy @Column(nullable = false, length = 100) private String createdBy; // 생성자
        @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt; // 수정일시
        @LastModifiedBy @Column(nullable = false, length = 100) private String modifiedBy; // 수정자
    }
    @Embedded Tmp tmp;
     */

/////////////////////////////////////////////////////////////////
    // 메타데이터
//    @CreatedDate
//    @Column(nullable = false)
//    private LocalDateTime createdAt; // 생성일시
//
//    @CreatedBy
//    @Column(nullable = false, length = 100)
//    private String createdBy; // 생성자

//    @LastModifiedDate
//    @Column(nullable = false)
//    private LocalDateTime modifiedAt; // 수정일시
//
//    @LastModifiedBy
//    @Column(nullable = false, length = 100)
//    private String modifiedBy; // 수정자
//////////////////////////////////////////////////////////////////
    /** 위에 처럼 어노테이션을 붙여주기만 하면 auditing이 작동한다.
     * @CreatedDate : 최초에 insert할때 자동으로 한번 넣어준다.
     * @CreatedBy : 최초에 insert할때 자동으로 한번 넣어준다.
     * @LastModifiedDate : 최초 또는 작성 당시의 시간을 실시간으로 넣어준다.
     * @LastModifiedBy : 작성 당시의 작성자의 이름을 실시간으로 넣어준다.
     */

    // 패턴디자인, 제일 어렵다
    /* Entity를 만들때는 무조건 기본 생성자가 필요하다.
    *  public 또는 protected만 가능한데, 평생 아무데서도 기본생성자를 안쓰이게 하고 싶어서 protected로 변경함
    * */
    protected Article() { }

    /* 사용자가 입력하는 값만 받기, 나머지는 시스템이 알아서 하게 해주면 됨 */
    private Article(UserAccount userAccount,String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
//        return id.equals(article.id);
//        return (article.id).equals(id);

        return id !=null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
