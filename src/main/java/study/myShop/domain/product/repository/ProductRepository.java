package study.myShop.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.product.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
    Optional<Product> findByName(String name);
}
