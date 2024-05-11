package study.myShop.domain.order.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import study.myShop.global.exception.BaseException;

@Getter
@AllArgsConstructor
public class ProductException extends BaseException {

    private final ProductExceptionType exceptionType;
}
