package study.myShop.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.order.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
    Optional<Product> findByName(String name);
}
