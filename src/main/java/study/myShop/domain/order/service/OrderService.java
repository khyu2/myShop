package study.myShop.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.dto.OrderRequest;
import study.myShop.domain.order.entity.Order;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.exception.OrderException;
import study.myShop.domain.order.exception.OrderExceptionType;
import study.myShop.domain.order.repository.OrderRepository;
import study.myShop.domain.order.repository.ProductRepository;
import study.myShop.domain.payment.entity.Payment;
import study.myShop.domain.payment.service.PaymentService;

import java.net.http.HttpHeaders;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final OrderProductService orderProductService;

    @Transactional
    public Long order(OrderRequest orderRequest, HttpHeaders headers) {

        // 주소
        String address = orderRequest.address();

        // 휴대폰 번호
        String tel = orderRequest.tel();
        String orderComment = orderRequest.orderComment();
        String recipient = orderRequest.recipient();


        // 주문이 OrderRequest로 들어오면 수신자 정보, 상품과 개수가 들어오는데
        // 상품과 개수를 리스트로 만들어 Order 에 저장한다
        List<OrderProduct> orderProducts = orderProductService.create(orderRequest.orderProductRequestList());

        // 결재 정보
        Payment payment = paymentService.create(orderRequest.paymentRequest());

        // header에서 유저 정보 꺼내 저장하기
        // pass

        Order order = Order.create(null, payment, orderProducts, address, orderComment, recipient, tel);

        return orderRepository.save(order).getId();
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderExceptionType.NOT_FOUND_ORDER)
        );

        order.cancel();
    }
}
