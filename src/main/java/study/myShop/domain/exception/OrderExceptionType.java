package study.myShop.domain.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import study.myShop.global.exception.BaseExceptionType;

@AllArgsConstructor
public enum OrderExceptionType implements BaseExceptionType {
    NOT_FOUND_ORDER(800, HttpStatus.FORBIDDEN, "주문 정보가 존재하지 않습니다"),
    ALREADY_COMPLETED(801, HttpStatus.FORBIDDEN, "이미 상품이 배송되었습니다");

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
