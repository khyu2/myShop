package study.myShop.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
