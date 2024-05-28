package study.myShop.domain.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import study.myShop.global.exception.BaseExceptionType;

@AllArgsConstructor
public enum CouponExceptionType implements BaseExceptionType {

    NOT_FOUND_COUPON(901, HttpStatus.FORBIDDEN, "쿠폰을 찾을 수 없습니다"),
    VALIDITY_PERIOD_EXPIRED(902, HttpStatus.FORBIDDEN, "쿠폰 유효기간이 만료되었습니다.");

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
