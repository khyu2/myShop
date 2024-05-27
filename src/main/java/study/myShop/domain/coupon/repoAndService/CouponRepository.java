package study.myShop.domain.coupon.repoAndService;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
