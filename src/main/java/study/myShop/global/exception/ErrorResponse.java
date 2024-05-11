package study.myShop.global.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String message, int errorCode) {

    public static ErrorResponse toResponse(BaseExceptionType exceptionType) {
        return new ErrorResponse(
                exceptionType.getHttpStatus(),
                exceptionType.getErrorMessage(),
                exceptionType.getErrorCode()
        );
    }
}