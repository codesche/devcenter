package org.com.newsletter.common.exception;

import java.nio.file.AccessDeniedException;
import java.util.Objects;
import org.com.newsletter.common.response.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 전역 예외 처리기.
 * - 컨트롤러 계층에서 발생한 예외를 공통 응답으로 반환해준다.
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<RsData<Void>> handleApi(ApiException e) {
        return ResponseEntity.status(e.getStatus())
            .body(RsData.error(e.getCode().name(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handleValid(MethodArgumentNotValidException e) {
        String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseEntity.badRequest()
            .body(RsData.error(ErrorCode.INVALID_REQUEST.name(), msg));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RsData<Void>> handleDenied(AccessDeniedException e) {
        return ResponseEntity.status(403)
            .body(RsData.error(ErrorCode.FORBIDDEN.name(), "접근 권한이 없습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleAny(Exception e) {
        return ResponseEntity.internalServerError()
            .body(RsData.error(ErrorCode.INTERNAL_ERROR.name(), e.getMessage()));
    }

}
