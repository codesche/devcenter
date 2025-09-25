package com.hospital.common.exception;

import com.hospital.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;
import javax.naming.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 전역 예외 처리를 담당하는 핸들러 클래스
 * 애플리케이션에서 발생하는 모든 예외를 일관된 형태로 처리하고 응답
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     * 도메인 로직에서 발생한 의미 있는 예외를 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Void> response = ApiResponse.error(e.getMessage(), errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 인증 예외 처리
     * Spring Security에서 발생하는 인증 관련 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication exception occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
        ApiResponse<Void> response = ApiResponse.error(errorCode.getMessage(), errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 잘못된 인증 정보 예외 처리
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("BadCredentialsException occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
        ApiResponse<Void> response = ApiResponse.error("이메일 또는 비밀번호가 올바르지 않습니다", errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 접근 거부 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("AccessDeniedException occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        ApiResponse<Void> response = ApiResponse.error(errorCode.getMessage(), errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * @Valid 검증 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation exception occurred: {}", e.getMessage(), e);

        String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ApiResponse<Void> response = ApiResponse.error(message, errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        log.warn("Bind exception occurred: {}", e.getMessage(), e);

        String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ApiResponse<Void> response = ApiResponse.error(message, errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 제약 조건 위반 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Constraint violation exception occurred: {}", e.getMessage(), e);

        String message = e.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ApiResponse<Void> response = ApiResponse.error(message, errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Method argument type mismatch exception occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INVALID_TYPE_VALUE;
        ApiResponse<Void> response = ApiResponse.error(errorCode.getMessage(), errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * HTTP 메서드 지원하지 않음 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HttpRequestMethodNotSupportedException occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        ApiResponse<Void> response = ApiResponse.error(errorCode.getMessage(), errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 필수 요청 파라미터 누락 예외 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("Missing servlet request parameter exception occurred: {}", e.getMessage(), e);

        String message = String.format("필수 파라미터 '%s'가 누락되었습니다", e.getParameterName());
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ApiResponse<Void> response = ApiResponse.error(message, errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * HTTP 메시지 읽기 불가 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Http message not readable exception occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ApiResponse<Void> response = ApiResponse.error("요청 데이터 형식이 올바르지 않습니다", errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 데이터 무결성 위반 예외 처리
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Data integrity violation exception occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ApiResponse<Void> response = ApiResponse.error("데이터 무결성 제약 조건 위반입니다", errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiResponse<Void> response = ApiResponse.error(errorCode.getMessage(), errorCode.getCode());

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(response);
    }

}
