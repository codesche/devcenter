package org.com.authproject.auth.common.exception;

import org.com.authproject.auth.common.api.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 전역 예외 처리: 클라이언트에게 일관된 응답을 제공 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ApiResponse<Void> handleDomain(DomainException e) {
        return ApiResponse.fail(e.getErrorCode().getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOthers(Exception e) {
        return ApiResponse.fail(ErrorCode.INTERNAL_ERROR.getMessage());
    }

}
