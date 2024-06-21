package study.myShop.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.exception.OrderException;
import study.myShop.domain.exception.OrderExceptionType;
import study.myShop.domain.payment.entity.Payment;

import java.time.LocalDateTime;
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
    private final List<OrderProduct> orderProducts = new ArrayList<>();

    private Long totalPrice;
    private String orderComment; // 주문 요청사항
    private LocalDateTime orderEnroll; // 주문 일자

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // 수취인 관련 정보
    private String recipient;
    private String address;
    private String tel;

    public void addOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
        orderProduct.update(); // 상품 재고 감소
    }

    public static Order create(Member member, Payment payment, List<OrderProduct> orderProducts,
                               String address, String orderComment, String recipient, String tel) {
        Order order = Order.builder()
                .member(member)
                .payment(payment)

                .orderComment(orderComment)
                .orderEnroll(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDER)

                .recipient(recipient)
                .address(address)
                .tel(tel)
                .build();

        // orderProducts 순회하며 양방향 추가
        orderProducts.forEach(order::addOrderProduct);

        return order;
    }

    public void cancel() {
        if (orderStatus == OrderStatus.SHIP || orderStatus == OrderStatus.COMPLETE) {
            throw new OrderException(OrderExceptionType.ALREADY_COMPLETED);
        }

        this.orderStatus = OrderStatus.CANCEL;
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.cancel();
        }
    }

    // 주문 상품 전체 가격 조회
    public long getTotalPrice() {
        long totalPrice = 0;
        for (OrderProduct orderProduct : orderProducts) {
            totalPrice += orderProduct.getTotalPrice();
        }
        return totalPrice;
    }
}

