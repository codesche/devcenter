package org.com.algosapi.common.exception;

public enum ErrorCode {
    INVALID_ARGUMENT(400, "Invalid argument"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal server error");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
