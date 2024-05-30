package study.myShop.domain.payment.repoAndService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.myShop.domain.payment.dto.PaymentRequest;
import study.myShop.domain.payment.entity.Payment;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment create(PaymentRequest paymentRequest) {
        return paymentRepository.save(paymentRequest.toEntity());
    }
}
