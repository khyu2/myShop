package study.myShop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.payment.entity.Payment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private Long totalPrice;
    private String orderComment; // 주문 요청사항
    private String orderEnroll; // 주문 일자

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // 수취인 관련 정보
    private String recipient;
    private String address;
    private String tel;

    public void addOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }
}
