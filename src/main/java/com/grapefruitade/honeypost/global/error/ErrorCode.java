package com.grapefruitade.honeypost.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // POST
    MAXIMUM_IMAGES_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지는 최대 7장까지 가능합니다."),
    INVALID_EXTENSION(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "잘못된 이미지 확장자 입니다."),
    INVALID_POST(HttpStatus.BAD_REQUEST, "유효 하지 않은 게시글 입니다."),

    // TOKEN
    TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED,"TOKEN_NOT_VALID"),
    TOKEN_IS_EXPIRATION(HttpStatus.UNAUTHORIZED,"TOKEN_IS_EXPIRATION"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN_NOT_FOUND"),

    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"),
    MISMATCH_USER_PASSWORD(HttpStatus.BAD_REQUEST, "MISMATCH_USER_PASSWORD"),
    ALREADY_EXIST_USERNAME(HttpStatus.CONFLICT, "ALREADY_EXIST_USERNAME"),

    // COMMENT
    INVALID_COMMENT(HttpStatus.BAD_REQUEST, " INVALID_COMMENT");

    private final HttpStatus httpStatus;
    private final String message;
}
