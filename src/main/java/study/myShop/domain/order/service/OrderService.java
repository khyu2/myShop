package study.myShop.domain.order.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.exception.MemberException;
import study.myShop.domain.member.exception.MemberExceptionType;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;
import study.myShop.domain.order.dto.OrderRequest;
import study.myShop.domain.order.entity.Order;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.exception.OrderException;
import study.myShop.domain.order.exception.OrderExceptionType;
import study.myShop.domain.order.repository.OrderRepository;
import study.myShop.domain.payment.entity.Payment;
import study.myShop.domain.payment.service.PaymentService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final JwtService jwtService;
    private final OrderProductService orderProductService;

    @Transactional
    public Long order(OrderRequest orderRequest, HttpServletRequest request) throws ServletException, IOException {

        // 주소
        String address = orderRequest.address();

        // 휴대폰 번호
        String tel = orderRequest.tel();
        String orderComment = orderRequest.orderComment();
        String recipient = orderRequest.recipient();

        // 주문이 OrderRequest로 들어오면 수신자 정보, 상품과 개수가 들어오는데
        // 상품과 개수를 리스트로 만들어 Order 에 저장한다
        List<OrderProduct> orderProducts = orderProductService.create(orderRequest.orderProductRequestList());
        for (OrderProduct orderProduct : orderProducts) {
            log.info(orderProduct.toString());
        }

        // 결재 정보
        Payment payment = paymentService.create(orderRequest.paymentRequest());

        // header에서 유저 정보 꺼내 저장하기
        String accessToken = jwtService.extractAccessToken(request);
        log.info(accessToken);
        String email = jwtService.extractUsername(accessToken);

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        Order order = Order.create(member, payment, orderProducts, address, orderComment, recipient, tel);

        return orderRepository.save(order).getId();
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderExceptionType.NOT_FOUND_ORDER)
        );

        order.cancel();
    }

    public Order getOne(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderExceptionType.NOT_FOUND_ORDER)
        );
    }
}
