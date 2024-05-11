package study.myShop.domain.payment.dto;

import study.myShop.domain.payment.entity.PaymentGateway;
import study.myShop.domain.payment.entity.PaymentMethod;
import study.myShop.domain.payment.entity.Payment;

public record PaymentRequest(
        PaymentMethod paymentMethod,
        PaymentGateway paymentGateway
) {
    public Payment toEntity() {
        return Payment.builder().serialNo(Payment.createSerialNo()).paymentMethod(paymentMethod)
                .paymentGateway(paymentGateway).build();
    }
}
