package study.myShop.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import study.myShop.domain.payment.dto.PaymentRequest;

import java.util.List;

/**
 * request example
 * payment - (신용 카드, 토스)
 * address - 서울시 ~~
 * tel - 010-1234-1234
 * recipient - Kim
 * orderProductRequestList - (1, 10), (2, 20) 1번 상품 10개, 2번 상품 20개
 * -> order~list 사용 대신 cart에서 위임받아서 주문 처리, cart는 request를 통해 member 객체로 넘어감
 */
public record OrderRequest(
        @NotNull PaymentRequest paymentRequest,
        @NotNull String orderComment,
        @NotNull String address,
        @NotNull String tel,
        @NotNull String recipient
//        @NotNull List<OrderProductRequest> orderProducts
) {
}
