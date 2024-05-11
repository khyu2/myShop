package study.myShop.domain.order.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import study.myShop.global.exception.BaseException;
import study.myShop.global.exception.BaseExceptionType;

@Getter
@AllArgsConstructor
public class OrderException extends BaseException {

    private final BaseExceptionType exceptionType;
}
