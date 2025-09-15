package org.com.algosapi.common.exception;

public class DomainException extends RuntimeException{
    private ErrorCode errorCode;

    public DomainException(ErrorCode errorCode, String detail) {
        super(detail);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
