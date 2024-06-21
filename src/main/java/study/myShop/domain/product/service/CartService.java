package study.myShop.domain.product.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.exception.ProductException;
import study.myShop.domain.exception.ProductExceptionType;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.exception.MemberException;
import study.myShop.domain.exception.MemberExceptionType;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;
import study.myShop.domain.order.service.OrderProductService;
import study.myShop.domain.product.entity.Cart;
import study.myShop.domain.product.entity.Product;
import study.myShop.domain.product.repository.CartRepository;
import study.myShop.domain.product.repository.ProductRepository;

import java.io.IOException;
import java.util.List;

/**
 * Cart 비즈니스 로직
 * - 모든 주문은 장바구니를 통해 이뤄진다
 * - 상품을 고르면 장바구니, 바로 구매 두가지 선택지가 주어진다
 * - 장바구니를 누르면 현재 상품을 장바구니로 넣는다
 * - 바로 구매를 누르면 장바구니에 있는 상품들 + 현재 상품을 장바구니에 추가한다
 * - 이후 결제...
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final MemberRepository memberRepository;
    private final OrderProductService orderProductService;
    private final CartRepository cartRepository;
    private final JwtService jwtService;
    private final ProductRepository productRepository;

    /* 장바구니에 상품 추가 */
    @Transactional
    public void insertProduct(Product product, HttpServletRequest request) {
        // 사용자 정보 추출
        String email = jwtService.getUsername(request);

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        // 사용자 Cart에 주문상품 추가
        member.addProductsInCart(product);
    }

    /**
     * 제거할 상품 목록
     * 사용자 장바구니 꺼내와서 List에서 제거
     */
    @Transactional
    public void removeProducts(Long productId, HttpServletRequest request) throws ServletException, IOException {
        String email = jwtService.getUsername(request);

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
        );

        /* 장바구니에서 상품 제거 */
        Cart cart = member.getCart();
        List<Product> cartProducts = cart.getProducts();

        cartProducts.remove(product);
    }

    public List<Product> getCartList(HttpServletRequest request) throws ServletException, IOException {
        String email = jwtService.getUsername(request);

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        Cart cart = member.getCart();
        return cart.getProducts();
    }
}
