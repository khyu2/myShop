package study.myShop.domain.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.dto.MemberDefaultDto;
import study.myShop.domain.member.service.JwtService;
import study.myShop.domain.member.service.MemberService;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.dto.OrderRequest;
import study.myShop.domain.order.dto.ProductRequest;
import study.myShop.domain.order.entity.Category;
import study.myShop.domain.order.entity.Order;
import study.myShop.domain.order.entity.Product;
import study.myShop.domain.order.exception.ProductException;
import study.myShop.domain.order.repository.OrderRepository;
import study.myShop.domain.payment.dto.PaymentRequest;
import study.myShop.domain.payment.entity.PaymentGateway;
import study.myShop.domain.payment.entity.PaymentMethod;

import java.io.IOException;
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
    @Autowired JwtService jwtService;

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() throws IOException {
        MemberDefaultDto memberDto = new MemberDefaultDto("ww@mail", "1234", null, "010-1234-4321", "Seoul", "mapo-gu");
        memberService.join(memberDto);
        ProductRequest productRequest = new ProductRequest("Apple", "Delicious Apple!", 2000L, 20L, Category.Food, null);
        productService.create(productRequest);

        String accessToken = jwtService.createAccessToken(memberDto.email());
        String refreshToken = jwtService.createRefreshToken();

        request = new MockHttpServletRequest();
        request.addHeader(accessHeader, "Bearer "+accessToken);
        request.addHeader(refreshHeader, "Bearer "+refreshToken);
    }

    private OrderRequest getOrderRequest() {
        PaymentRequest paymentRequest = new PaymentRequest(PaymentMethod.CREDIT_CARD, PaymentGateway.SAMSUNG);
        OrderProductRequest orderProductRequest = new OrderProductRequest(1L, 2L);
        List<OrderProductRequest> orderProductRequestList = new ArrayList<>();
        orderProductRequestList.add(orderProductRequest);

        return new OrderRequest(paymentRequest, "집앞에 놔주세요", "Seoul", "010-1234-4142",
                "Kim", orderProductRequestList);
    }

    @Test
    void 상품주문() throws Exception {
        //given
        OrderRequest orderRequest = getOrderRequest();

        //when
        Long orderId = orderService.order(orderRequest, request);
        Order order = orderService.getOne(orderId);

        //then
        assertEquals(order.getOrderComment(), orderRequest.orderComment());
        assertEquals(order.getMember().getEmail(), "ww@mail");
    }

    @Test
    void 여러개_상품주문_실패() throws Exception {
        //given
        OrderRequest orderRequest = getOrderRequest();
        List<OrderProductRequest> orderProductRequests = orderRequest.orderProductRequestList();
        orderProductRequests.add(new OrderProductRequest(2L, 10L));

        //when, then
        assertThrows(ProductException.class, () -> orderService.order(orderRequest, request));
    }

    @Test
    void 여러개_상품주문_성공() throws Exception {
        //given
        ProductRequest banana = new ProductRequest("Banana", "Delicious Banana!", 5000L, 100L, Category.Food, null);
        ProductRequest book = new ProductRequest("Spring-Book", "Cool", 35000L, 10L, Category.Books, null);
        productService.create(banana);
        productService.create(book);

        OrderRequest orderRequest = getOrderRequest();
        List<OrderProductRequest> orderProductRequests = orderRequest.orderProductRequestList();
        orderProductRequests.add(new OrderProductRequest(2L, 10L));

        //when
        Long orderId = orderService.order(orderRequest, request);
        Order order = orderService.getOne(orderId);

        //then
        // Apple * 2 = 4000, banana * 10 = 50000, totalPrice = 54000
        assertEquals(order.getTotalPrice(), 54000);

        //주문 시 재고 깍임
        Product product = productService.getOne(2L);
        assertEquals(product.getStock(), 90);
    }
}