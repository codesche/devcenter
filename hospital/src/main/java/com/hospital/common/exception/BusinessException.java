package com.hospital.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 처리 중 발생하는 예외를 나타내는 커스텀 예외 클래스
 * 도메인 로직에서 발생하는 의미 있는 예외 상황을 표현
 */
@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode와 커스텀 메시지로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param message   커스텀 메시지
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode, 커스텀 메시지, 원인 예외로 예외 생성
     * @param errorCode 에러 코드
     * @param message   커스텀 메시지
     * @param cause     원인 예외
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
