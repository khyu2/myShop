package study.myShop.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.product.entity.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMemberId(Long memberId);
    boolean existsByMemberId(Long memberId);
}
