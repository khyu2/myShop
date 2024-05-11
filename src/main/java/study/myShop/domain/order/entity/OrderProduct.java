package study.myShop.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Long orderPrice;
    private Long count;

    public static OrderProduct createOrderProduct(Product product, Long count) {
        OrderProduct orderProduct = new OrderProduct();

        orderProduct.setProduct(product); // 주문 상품
        orderProduct.setCount(count); // 주문 수량

        // 상품 가격 세팅. 추후 쿠폰이나 할인 등 적용 예정
        orderProduct.setOrderPrice(product.getPrice());

        product.removeStock(count);
        return orderProduct;
    }

    public long getTotalPrice() {
        return orderPrice * count;
    }
}
