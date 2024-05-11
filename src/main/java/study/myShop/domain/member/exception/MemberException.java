package study.myShop.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import study.myShop.global.exception.BaseException;
import study.myShop.global.exception.BaseExceptionType;

@Getter
@AllArgsConstructor
public class MemberException extends BaseException {

    private final BaseExceptionType exceptionType;
}