package study.myShop.domain.order.service;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.dto.MemberDefaultDto;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.service.JwtService;
import study.myShop.domain.member.service.MemberService;
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
import study.myShop.domain.product.exception.ProductException;
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
    @Autowired OrderProductService orderProductService;
    @Autowired CartService cartService;
    @Autowired JwtService jwtService;

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    MockHttpServletRequest request;

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
        ProductRequest productRequest = new ProductRequest("Apple", "Delicious Apple!", 2000L, 20L, Category.Food, null);
        productService.create(productRequest);
        Product apple = productService.getOne(1L);

        // 주문 상품 추가
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(OrderProduct.createOrderProduct(apple, 10L));

        // 장바구니에 주문 상품 추가
        cartService.insertProducts(orderProducts, request);
    }

    private OrderRequest getOrderRequest() {
        PaymentRequest paymentRequest = new PaymentRequest(PaymentMethod.CREDIT_CARD, PaymentGateway.SAMSUNG);

        return new OrderRequest(paymentRequest, "집앞에 놔주세요", "Seoul", "010-1234-4142",
                "Kim");
    }

    @Test
    void 상품주문() throws Exception {
        //given
        OrderRequest orderRequest = getOrderRequest();

        //when
        Long orderId = orderService.order(orderRequest, request);
        Order order = orderService.getOne(orderId);

        //then
        // 주문 시, 주문한 사용자의 장바구니에 주문상품이 잘 추가되었는지 확인
        assertEquals(1, order.getMember().getCart().getOrderProducts().size());

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
    void 주문_상품재고부족_실패() throws Exception {
        //given

        // 주문 상품 30개 추가 -> 기존 재고 20개
        Product apple = productService.getOne(1L);
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(OrderProduct.createOrderProduct(apple, 30L));

        cartService.insertProducts(orderProducts, request);

        //when, then
        assertThrows(ProductException.class, () -> orderService.order(getOrderRequest(), request));
    }

    @Test
    void 여러개_상품주문_성공() throws Exception {
        //given
        ProductRequest banana = new ProductRequest("Banana", "Delicious Banana!", 5000L, 100L, Category.Food, null);
        ProductRequest book = new ProductRequest("Spring-Book", "Cool", 35000L, 10L, Category.Books, null);
        productService.create(banana);
        productService.create(book);
        List<Product> products = productService.getAllProducts();

        OrderRequest orderRequest = getOrderRequest();
        Member member = memberService.getOne(1L);
        List<OrderProduct> orderProducts = member.getCart().getOrderProducts();

        for (Product product : products) {
            orderProducts.add(OrderProduct.createOrderProduct(product, 2L));
        }

        //when
        Long orderId = orderService.order(orderRequest, request);
        Order order = orderService.getOne(orderId);

        //then
        // Apple * 12 = 24000, banana * 2 = 10000, book * 2 = 70000, total = 104,000
        assertEquals(104000L, order.getTotalPrice());

        //주문 시 재고 깍임 100 - 2 = 98
        Product product = productService.getOne(2L);
        assertEquals(product.getStock(), 98);
    }
}