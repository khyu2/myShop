package study.myShop.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
