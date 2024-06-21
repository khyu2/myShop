package study.myShop.domain.order.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.coupon.repoAndService.CouponService;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.exception.MemberException;
import study.myShop.domain.exception.MemberExceptionType;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.dto.OrderRequest;
import study.myShop.domain.order.entity.Order;
import study.myShop.domain.exception.OrderException;
import study.myShop.domain.exception.OrderExceptionType;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.repository.OrderRepository;
import study.myShop.domain.payment.entity.Payment;
import study.myShop.domain.payment.repoAndService.PaymentService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final CouponService couponService;
    private final OrderProductService orderProductService;
    private final JwtService jwtService;

    /**
     * order, 주문 정보, request, 주문상품 리스트가 들어온다
     * 주문상품 리스트는 프론트 단에서 List 형태로 받아온다.
     * 혹은 Controller 단에서 json 형태로 온 것을 파싱해서 전달받음
     */
    @Transactional
    public Long order(OrderRequest orderRequest, HttpServletRequest request) {

        // 사용자 정보
        String address = orderRequest.address();
        String tel = orderRequest.tel();
        String orderComment = orderRequest.orderComment();
        String recipient = orderRequest.recipient();

        // 결재 정보
        Payment payment = paymentService.create(orderRequest.paymentRequest());

        // header에서 유저 정보 꺼내 저장하기
        String accessToken = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_TOKEN));
        String email = jwtService.extractUsername(accessToken)
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        // coupon 적용된 orderProducts 가져오기
        List<OrderProductRequest> orderProductRequests = orderRequest.orderProductRequests();
        List<OrderProduct> orderProducts = orderProductService.create(orderProductRequests);

        Order order = Order.create(member, payment, orderProducts, address, orderComment, recipient, tel);

        return orderRepository.save(order).getId();
    }

    /**
     * 주문 취소 시 주의사항
     * - 상품 재고 += 주문상품 개수
     * - 쿠폰 사용 취소 -> 사용자 쿠폰 활성화
     * - ...
     */
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
