package org.com.algosapi.common.exception;

import java.net.BindException;
import org.com.algosapi.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<?>> handleValidation(Exception ex){
        return ResponseEntity.badRequest().body(ApiResponse.fail("Validation failed: " + ex.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<?>> handleDomain(DomainException ex){
        var code = ex.getErrorCode();
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.fail(code.getMessage()+": "+ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOthers(Exception ex){
        return ResponseEntity.internalServerError().body(ApiResponse.fail("Unexpected error"));
    }

}
