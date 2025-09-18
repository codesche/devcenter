package org.com.newsletter.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode code;
    private final HttpStatus status;

    public ApiException(ErrorCode code, String message) {
        super(message);
        this.code = code;
        this.status = map(code);
    }

    public ApiException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = map(code);
    }

    private HttpStatus map(ErrorCode code) {
        switch (code) {
            case INVALID_REQUEST: {
                return HttpStatus.BAD_REQUEST;
            }

            case UNAUTHORIZED: {
                return HttpStatus.UNAUTHORIZED;
            }

            case FORBIDDEN: {
                return HttpStatus.FORBIDDEN;
            }

            case NOT_FOUND: {
                return HttpStatus.NOT_FOUND;
            }

            case CONFLICT: {
                return HttpStatus.CONFLICT;
            }

            case EXTERNAL_API_ERROR: {
                return HttpStatus.BAD_GATEWAY;
            }

            case RATE_LIMITED: {
                return HttpStatus.TOO_MANY_REQUESTS;
            }

            case INTERNAL_ERROR:
            default: {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }

}
