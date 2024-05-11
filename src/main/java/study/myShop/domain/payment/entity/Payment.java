package study.myShop.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import study.myShop.domain.order.entity.Order;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private String serialNo; // 결제 고유 번호 반환

    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToOne(mappedBy = "payment")
    private Order order; // 주문 정보 확인

    public static String createSerialNo() {
        return UUID.randomUUID().toString();
    }
}
