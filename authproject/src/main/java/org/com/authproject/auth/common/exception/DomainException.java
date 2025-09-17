package org.com.authproject.auth.common.exception;

import lombok.Getter;

/**
 * 의미 있는 도메인 예외의 베이스 클래스
 */
@Getter
public class DomainException extends RuntimeException{

    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
