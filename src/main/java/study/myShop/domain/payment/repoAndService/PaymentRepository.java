package study.myShop.domain.payment.repoAndService;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
