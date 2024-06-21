package study.myShop.domain.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.entity.MemberStatus;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.product.entity.Category;
import study.myShop.domain.product.entity.Product;
import study.myShop.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired CartService cartService;
    @Autowired ProductRepository productRepository;
    @Autowired JwtService jwtService;
    @Autowired MemberRepository memberRepository;

    MockHttpServletRequest request;

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    @BeforeEach
    void init() {
        Product apple = new Product(1L, "Apple", "Delicious Apple", 1000L, 20L, Category.Food, null);
        Product book = new Product(2L, "Spring book", "!!!", 35000L, 35L, Category.Books, null);
        productRepository.save(apple);
        productRepository.save(book);

        Member member = new Member(1L, null, "ww@mail", "1234",
                MemberStatus.USER, "010-1234-1421", "Seoul", "mapo", null, null, null);
        memberRepository.save(member);

        String accessToken = jwtService.createAccessToken(member.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        request = new MockHttpServletRequest();
        request.addHeader(accessHeader, "Bearer "+accessToken);
        request.addHeader(refreshHeader, "Bearer "+refreshToken);
    }

    @Test
    @Order(100)
    void 장바구니_상품_추가() throws Exception {
        //given
        List<Product> products = productRepository.findAll();

        //when
        products.forEach(product -> cartService.insertProduct(product, request));
        List<Product> cartList = cartService.getCartList(request);

        //then
        cartList.forEach(System.out::println);
        assertEquals(2, cartList.size());
    }

    @Test
    @Order(200)
    void 장바구니_상품_제거() throws Exception {
        //given
        String username = jwtService.getUsername(request);

        //when
        Product apple = productRepository.findByName("Apple").get();

        cartService.removeProducts(apple.getId(), request);
        List<Product> cartList = cartService.getCartList(request);

        //then
        cartList.forEach(System.out::println);
        assertEquals(1, cartList.size());
        assertEquals("Spring book", cartList.get(0).getName());
    }
}