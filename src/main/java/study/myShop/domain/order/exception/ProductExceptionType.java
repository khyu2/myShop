package study.myShop.domain.order.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import study.myShop.global.exception.BaseExceptionType;

@AllArgsConstructor
public enum ProductExceptionType implements BaseExceptionType {

    PRODUCT_OUT_OF_STOCK(700, HttpStatus.FORBIDDEN, "물품 수량이 부족합니다"),
    ALREADY_EXIST_PRODUCT(701, HttpStatus.FORBIDDEN, "이미 상품이 존재합니다"),
    NOT_FOUND_PRODUCT(702, HttpStatus.FORBIDDEN, "상품이 존재하지 않습니다");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
