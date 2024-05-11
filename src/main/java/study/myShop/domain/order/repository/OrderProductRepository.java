package study.myShop.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.order.entity.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
