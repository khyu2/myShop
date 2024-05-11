package study.myShop.domain.order.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import study.myShop.global.exception.BaseExceptionType;

@AllArgsConstructor
public enum OrderExceptionType implements BaseExceptionType {
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
