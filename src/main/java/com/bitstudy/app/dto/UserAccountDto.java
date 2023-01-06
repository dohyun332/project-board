package com.bitstudy.app.dto;

import com.bitstudy.app.domain.Article;
import com.bitstudy.app.domain.UserAccount;

import java.time.LocalDateTime;

public record UserAccountDto(
        Long id,
        String user_id,
        String user_password,
        String email,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static UserAccountDto of(Long id, String user_id, String user_password, String email, String nickname, String memo, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto(id, user_id, user_password, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static UserAccountDto from(UserAccount entity) {
       return new UserAccountDto(
               entity.getId(),
               entity.getUser_id(),
               entity.getUser_password(),
               entity.getEmail(),
               entity.getNickname(),
               entity.getMemo(),
               entity.getCreatedAt(),
               entity.getCreatedBy(),
               entity.getModifiedAt(),
               entity.getModifiedBy()
       );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
                user_id,
                user_password,
                email,
                nickname,
                memo
        );
    }

}
