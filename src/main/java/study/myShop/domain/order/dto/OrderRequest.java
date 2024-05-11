package study.myShop.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import study.myShop.domain.payment.dto.PaymentRequest;

import java.util.List;

public record OrderRequest(
        @NotNull PaymentRequest paymentRequest,
        @NotNull String address,
        @NotNull String tel,
        @NotNull String recipient,
        List<OrderProductRequest> orderProductRequests
) {
}
