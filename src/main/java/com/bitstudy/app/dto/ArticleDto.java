package com.bitstudy.app.dto;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;

import java.time.LocalDateTime;

/** record란
    자바 16버전에서 새로 나온거.
    DTO랑 비슷. DTO를 구현하려면 getter, setter, equals, hashcode, toString 같은 데이터 처리를 수행하기 위해
    오버라이드 된 메소드를 반복해서 작성해야한다. 이런것들은 보일러 플레이트 코드(여러곳에서 재사용되는 반복적으로 비슷한 형태를 가진 코드)라고 한다.

    롬복을 이용해서 어느정도 중복으로 발생하는 코드를 줄일수 있지만, 근본적인 한계는 해결 못한다.
    그래서 특정 데이터와 관련 있는 필드들만 묶어놓은 자료구조로 record 라는게 생겼다.

    getter를 제외한 나머지 기능들은 막혀있다.
 * 주의 : record는 entity로 쓸 수 없다. DTO로만 가능
         이유는 쿼리 결과를 매핑할때 객체를 인스턴스화 할 수 있도록 매개변수가 없는 생성자가 필요하지만, record에서는
         매개변수가 없는 생성자(기본생성자)를 제공하지 않는다.
         setter도 사용할 수 없다.(그래서 모든 필드의 값을 입력한 후에 생성할 수 있다.)
         - domain에 대해 필요한 것만 가져온거라고 간단히 보면 그렇다고 할 수 있다.
 */


public record ArticleDto( /* 우선 엔티티가 가지고 있는 모든 정보를 dto도 가지고 있게 해서 나중에 응답할때 어떤 걸 보내줄지 선택해서 가공하게 할거임*/
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
        ) {

//   private 생성자가 있는 것으로 판단하여 동작(of로 private 생성자 호출), 평소에 작업하는 dto라 생각(getter, setter, toString 다처리해줌)
    public static ArticleDto of(
            Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy
    ) {
        return new ArticleDto(id, userAccountDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    /** entity를 매개변수로 입력하면 ArticleDto로 변환해주는 메서드.
     *
     * entity를 받아서 new한 다음에 인스턴스에다가 entity. 이라고 해가면서 맵핑시켜서 return 하고 있는거
     * 맵퍼라고 부름(맵핑시키는거라고 생각)
     * 엔티티가 먼저이다.
     * */
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    /** 위에거랑 반대. dto를 주면 엔티티를 생서하는 메서드 */
    public Article toEntity() {
        return Article.of(
                userAccountDto.toEntity(),
                title,
                content,
                hashtag
        );
    }
}
