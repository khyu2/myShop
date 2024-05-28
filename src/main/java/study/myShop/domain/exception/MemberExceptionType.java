package study.myShop.domain.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import study.myShop.global.exception.BaseExceptionType;

@AllArgsConstructor
public enum MemberExceptionType implements BaseExceptionType {

    NOT_FOUND_MEMBER(600, HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_TOKEN(600, HttpStatus.BAD_REQUEST, "사용자 토큰을 찾을 수 없습니다."),
    ALREADY_EXIST_USERNAME(601, HttpStatus.FORBIDDEN, "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_TEL(602, HttpStatus.FORBIDDEN, "이미 존재하는 전화번호입니다."),
    WRONG_PASSWORD(603, HttpStatus.FORBIDDEN, "잘못된 비밀번호입니다."),
    PASSWORD_NOT_EQUAL(604, HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");

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
