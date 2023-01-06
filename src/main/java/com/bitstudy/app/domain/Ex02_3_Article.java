package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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

/** 할일: Lombok 사용하기
 *  주의: maven때랑 같은 방식인 것들도 이름이 다르게 되어 있으니 헷갈리지 않게 주의!
 *  class를 엔티티로 바꿔줄거다
 *
 * 순서
 * 1) 롬복을 이용해서 클래스를 앤티티로 변경
 * 2) getter/setter, toString 등의 롬복 어노테이션 사용
 * 3) 동등성, 동일성 비교할 수 있는 코드 넣어볼거임
 */

/* @Table - 엔티티와 매핑할 정보를 지정하고
            사용법) @Index(name="원하는명칭", columnList="사용할 테이블명")
                   name 부분을 생략하면 원래이름 사용
   @Index - 데이터베이스 인덱스는 추가, 쓰기 및 저장공간을 소모하여 테이블에 대한 데이터 검색속도를 향상시키는 데이터 구조
            @Entity와 세트로 사용
 */
@EntityListeners(AuditingEntityListener.class) /* 이거없으면 테스트할때 createdAt 때문에 에러남(Ex04 관련)*/
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
}) // 용량때문에 성능이 느려질수 있으니 잘사용하는것만, 검색 속도 증가시키기위한 색인
//@Entity
/* 1) 롬복을 이용해서 클래스를 앤티티로 변경 @Entity가 붙은 클래스는 JPA가 관라하게된다.
              그래서 기본키(PK)가 뭔지 알려줘야한다. 그게 @Id 에너테이션이다. */
@Getter // 롬복에 쓰면 알아서 모든 필드의 getter들이 생성
@ToString
// lazyload spring은 최초 실행에는 bean등 설정 파일 불러오기때문에 느리다
// 개별 인스턴스 앞에 쓰면 개별적으로 getter setter toString 넣을 수 있다.
/* 2) getter/setter, toString 등의 롬복 어노테이션 사용 */
public class Ex02_3_Article {

    @Id // 전체 필드 중에서 이것이 pk라고 지정, @Id가 없으면 @Entity 에러 발생
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 해당 필드가 auto_increment 인경우 @GeneratedValue를 사용하여 자동으로 값이 생성되게함. "기본키 전략"이라고 함
    private Long id;

    /* @Setter도 @Getter 처럼 클래스 단위로 걸수 있는데, 그렇게 하면 모든 필드에 접근이 가능해진다.
    *  그런데 id 같은 경우에는 내가 부여하는게 아니라 JPA에서 자동으로 부여해주는 번호이다.
    *  메타데이터들도 자동으로 JPA가 자동으로 세팅하게 만들어야한다. 그래서 id와 메타데이터는 @Setter가 필요없다.
    *  @Setter의 경우는 지금처럼 필요한 필드에만 주는걸 연습하자(이건 강사님스타일이고 회사마다 다름)
    * */
    /** @Column - 해당 컬럼이 not null인 경우 @Column(nullable = false) 써준다.
     *  기본값은 true라서 @Column을 아예 안쓰면 null가능
     *  @Column(nullable = false, length="숫자") 숫자 안쓰면 기본값 255 적용된다.
     */
    @Setter
    @Column(nullable = false)
    private String title; // 제목, not null

    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 본문, varchar안써주면 255먹음

    @Setter
    private String hashtag; // 해시태그

    /** 양방향 바인딩 */
    @OrderBy("id") // 양방향 바인딩을 할건데 정렬 기준을 id로 하겠다는 뜻
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    // 메타데이터
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @CreatedBy
    @Column(nullable = false, length = 100)
    private String createdBy; // 생성자
    /** 다른 생성일시 같은것들은 알아낼 수있는데, 최초 생성자는 (현재 코드에서는)인증받고 오지 않았기 때문에
     *  따로 알아낼 수가 없다. 좀전에 만든 JpaConfig파일을 사용한다. */

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일시

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; // 수정자

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
    protected Ex02_3_Article() { }

    /* 사용자가 입력하는 값만 받기, 나머지는 시스템이 알아서 하게 해주면 됨 */
    private Ex02_3_Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    /** 정적 팩토리 메서드(factory method pattern 중에 하나)
     *  정적 팩토리 메서드란 객체 생성 역할을 하는 클래스 메서드 라는 뜻
     *  of메서드를 이용해서 위에 있는 private 생성자를 직접적으로 사용해서 객체를 생성하는 방법
     *  !! 중요 : 무조건 static으로 놔야한다. !!
     *
     *  장점
     *   1) static이기 때문에 new를 이용하여 생성자를 만들지 않아도 된다.
     *   2) return을 가지고 있기 때문에 상속 시 값을 확인할 수 있다.
     *   3) (중요)객체 생성을 캡슐화 할 수 있다.
     */
    public static Ex02_3_Article of(String title, String content, String hashtag) {
        return new Ex02_3_Article(title, content, hashtag);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ex02_3_Article article = (Ex02_3_Article) o;
//        return id.equals(article.id);
//        return (article.id).equals(id);

        return id !=null && id.equals(article.id);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
