package org.com.authproject.auth.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    MEMBER_DUPLICATED("이미 존재하는 사용자입니다."),
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    AUTH_INVALID_CREDENTIALS("아이디 또는 비밀번호가 올바르지 않습니다."),
    AUTH_TOKEN_EXPIRED("토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID("유효하지 않은 토큰입니다."),
    AUTH_REFRESH_NOT_FOUND("RefreshToken이 존재하지 않습니다."),
    AUTH_REFRESH_MISMATCH("RefreshToken이 일치하지 않습니다."),
    FORBIDDEN("접근 권한이 없습니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    INTERNAL_ERROR("서버 오류가 발생했습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

}
