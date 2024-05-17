package study.myShop.domain.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.dto.MemberDefaultDto;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.service.MemberService;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.dto.OrderRequest;
import study.myShop.domain.order.dto.ProductRequest;
import study.myShop.domain.order.entity.Category;
import study.myShop.domain.order.entity.Order;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.repository.OrderRepository;
import study.myShop.domain.payment.dto.PaymentRequest;
import study.myShop.domain.payment.entity.Payment;
import study.myShop.domain.payment.entity.PaymentGateway;
import study.myShop.domain.payment.entity.PaymentMethod;
import study.myShop.domain.payment.service.PaymentService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Autowired MemberService memberService;
    @Autowired ProductService productService;
    @Autowired OrderProductService orderProductService;

    @Test
    void 상품주문() throws Exception {
        //given
        MemberDefaultDto memberDto = new MemberDefaultDto("ww@mail", "1234", null, "010-1234-4321", "Seoul", "mapo-gu");
        Long memberId = memberService.join(memberDto);
        ProductRequest productRequest = new ProductRequest("Apple", "Delicious Apple!", 2000L, 20L, Category.Food, null);
        productService.create(productRequest);
        PaymentRequest paymentRequest = new PaymentRequest(PaymentMethod.CREDIT_CARD, PaymentGateway.SAMSUNG);

        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        List<OrderProductRequest> orderProductRequestList = new ArrayList<>();
        orderProductRequestList.add(orderProductRequest);

        OrderRequest orderRequest = new OrderRequest(paymentRequest, "집앞에 놔주세요", "Seoul", "010-1234-3214", "Kim", orderProductRequestList);

        //when
        Long orderId = orderService.order(orderRequest, null);
        Order order = orderService.getOne(orderId);

        //then
        assertEquals(order.getOrderComment(), orderRequest.orderComment());
    }
}