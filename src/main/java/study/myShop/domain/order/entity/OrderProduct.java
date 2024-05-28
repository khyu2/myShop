package study.myShop.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.myShop.domain.coupon.entity.Coupon;
import study.myShop.domain.product.entity.Cart;
import study.myShop.domain.product.entity.Product;

@Entity
@Getter @Setter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private Long orderPrice;
    private Long count;

    private boolean couponCheck; // 쿠폰 사용 여부
    private Long couponId;

    public static OrderProduct createOrderProduct(Product product, Long count) {
        OrderProduct orderProduct = new OrderProduct();

        orderProduct.setProduct(product); // 주문 상품
        orderProduct.setCount(count); // 주문 수량

        // 상품 가격 세팅. 추후 쿠폰이나 할인 등 적용 예정
        orderProduct.setOrderPrice(product.getPrice());

        // 주문 상품을 만들 때 재고가 감소하면 안됨 -> Order 가 실행되었을 때 감소
        // if (count != null) product.removeStock(count);
        return orderProduct;
    }

    public static OrderProduct createOrderProduct(Product product, Long count, Coupon coupon) {
        OrderProduct orderProduct = new OrderProduct();

        orderProduct.setProduct(product);
        orderProduct.setCount(count);

        // coupon 이 들어오면 쿠폰 할인 적용
        Long discountedPrice = coupon.getDiscountedPrice(product.getPrice());
        orderProduct.setOrderPrice(discountedPrice);

        return orderProduct;
    }

    public void update() {
        product.removeStock(count);
    }

    public void cancel() {
        getProduct().addStock(count);
    }

    public long getTotalPrice() {
        return orderPrice * count;
    }

    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
                ", orderPrice=" + orderPrice +
                ", count=" + count +
                '}';
    }
}
