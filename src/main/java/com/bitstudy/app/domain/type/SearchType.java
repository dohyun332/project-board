package com.bitstudy.app.domain.type;

import lombok.Getter;

public enum SearchType {
//    제목, 본문, id, 글쓴이 ,해시태그
//    TITLE, CONTENT, ID, NICKNAME, HASHTAG
    TITLE("제목2"),
    CONTENT("본문2"),
    ID("유저아이디2"),
    NICKNAME("닉네임2"),
    HASHTAG("해시태그2");

    // name(description)

    @Getter
    private final String description;


    SearchType(String description) {
        this.description = description;
    }
}
