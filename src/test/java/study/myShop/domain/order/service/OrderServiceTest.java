package study.myShop.domain.order.service;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.coupon.dto.CouponRequest;
import study.myShop.domain.coupon.repoAndService.CouponService;
import study.myShop.domain.member.dto.MemberDefaultDto;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.service.JwtService;
import study.myShop.domain.member.service.MemberService;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.dto.OrderRequest;
import study.myShop.domain.order.entity.Order;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.repository.OrderRepository;
import study.myShop.domain.payment.dto.PaymentRequest;
import study.myShop.domain.payment.entity.PaymentGateway;
import study.myShop.domain.payment.entity.PaymentMethod;
import study.myShop.domain.product.dto.ProductRequest;
import study.myShop.domain.product.entity.Category;
import study.myShop.domain.product.entity.Product;
import study.myShop.domain.exception.ProductException;
import study.myShop.domain.product.service.CartService;
import study.myShop.domain.product.service.ProductService;

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
    @Autowired CartService cartService;
    @Autowired JwtService jwtService;
    @Autowired CouponService couponService;

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    MockHttpServletRequest request;
    PaymentRequest paymentRequest = new PaymentRequest(PaymentMethod.CREDIT_CARD, PaymentGateway.SAMSUNG);


    @BeforeEach
    void setUp() throws IOException, ServletException {
        // 사용자 정보 추가
        MemberDefaultDto memberDto = new MemberDefaultDto("ww@mail", "1234", null, "010-1234-4321", "Seoul", "mapo-gu");
        memberService.join(memberDto);
        String accessToken = jwtService.createAccessToken(memberDto.email());
        String refreshToken = jwtService.createRefreshToken();

        request = new MockHttpServletRequest();
        request.addHeader(accessHeader, "Bearer "+accessToken);
        request.addHeader(refreshHeader, "Bearer "+refreshToken);

        // 상품 추가
        ProductRequest apple = new ProductRequest("Apple", "Delicious Apple!", 2000L, 20L, Category.Food, null);
        ProductRequest banana = new ProductRequest("Banana", "Delicious Banana!", 3000L, 10L, Category.Food, null);
        ProductRequest book = new ProductRequest("Book", "Cool!", 15000L, 10L, Category.Books, null);
        productService.create(apple);
        productService.create(banana);
        productService.create(book);
//
//        // 장바구니에 주문 상품 추가
//        cartService.insertProducts(orderProductRequests, request);
    }
//
//    private OrderRequest getOrderRequest() {
//        PaymentRequest paymentRequest = new PaymentRequest(PaymentMethod.CREDIT_CARD, PaymentGateway.SAMSUNG);
//        CouponRequest couponRequest = new CouponRequest("생일 쿠폰", "생일 축하", 1000L, null, 1L);
//        couponService.issue(couponRequest);
//
//        return new OrderRequest(paymentRequest, "집앞에 놔주세요", "Seoul", "010-1234-4142",
//                "Kim", couponRequest);
//    }

    @Test
    void 상품주문_쿠폰적용X() throws Exception {
        //given
        // 주문 상품 추가
        List<OrderProductRequest> orderProductRequests = new ArrayList<>();
        orderProductRequests.add(new OrderProductRequest(1L, 10L, false, null)); // 사과 10개 20000원

        OrderRequest orderRequest = new OrderRequest(paymentRequest, "집앞에 놔주세요",
                "Seoul", "010-1234-4123", "Kim", orderProductRequests);

        //when
        Long orderId = orderService.order(orderRequest, request);
        Order order = orderService.getOne(orderId);

        //then
        // 주문 시, 주문한 사용자의 장바구니에 주문상품이 잘 삭제되었는지 확인
        assertEquals(1, order.getMember().getCart().getProducts().size());

        // 주문표의 주문 상품이 잘 저장되었는지 확인
        assertEquals(1, order.getOrderProducts().size());

        // 상품 주문 시, 기존 상품의 재고가 줄어들었는지 확인. 기존 20 - 10 = 10개
        Product one = productService.getOne(1L);
        assertEquals(10, one.getStock());

        // 주문 관련 테스트
        assertEquals(order.getOrderComment(), orderRequest.orderComment());
        assertEquals(order.getMember().getEmail(), "ww@mail");
    }

    @Test
    void 상품주문시_할인적용() throws Exception {

    }

//    @Test
//    void 주문_상품재고부족_실패() throws Exception {
//        //given
//
//        // 주문 상품 30개 추가 -> 기존 재고 20개
//        List<OrderProductRequest> orderProductRequestList = new ArrayList<>();
//        orderProductRequestList.add(new OrderProductRequest(1L, 30L, false, null));
//
//        cartService.insertProducts(orderProductRequestList, request);
//
//        //when, then
//        assertThrows(ProductException.class, () -> orderService.order(getOrderRequest(), request));
//    }

//    @Test
//    void 여러개_상품주문_성공() throws Exception {
//        //given
//        ProductRequest banana = new ProductRequest("Banana", "Delicious Banana!", 5000L, 100L, Category.Food, null);
//        ProductRequest book = new ProductRequest("Spring-Book", "Cool", 35000L, 10L, Category.Books, null);
//        productService.create(banana);
//        productService.create(book);
//        List<Product> products = productService.getAllProducts();
//
//        OrderRequest orderRequest = getOrderRequest();
//        Member member = memberService.getOne(1L);
//        List<OrderProduct> orderProducts = member.getCart().getOrderProducts();
//
//        for (Product product : products) {
//            orderProducts.add(OrderProduct.createOrderProduct(product, 2L));
//        }
//
//        //when
//        Long orderId = orderService.order(orderRequest, request);
//        Order order = orderService.getOne(orderId);
//
//        //then
//        // Apple * 12 = 24000, banana * 2 = 10000, book * 2 = 70000, total = 104,000
//        assertEquals(104000L, order.getTotalPrice());
//
//        //주문 시 재고 깍임 100 - 2 = 98
//        Product product = productService.getOne(2L);
//        assertEquals(product.getStock(), 98);
//    }

    // 상품 취소 테스트 추가 예정
}