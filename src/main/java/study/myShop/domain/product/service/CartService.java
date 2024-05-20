package study.myShop.domain.product.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.exception.MemberException;
import study.myShop.domain.member.exception.MemberExceptionType;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.product.entity.Cart;
import study.myShop.domain.product.repository.CartRepository;

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
    private final CartRepository cartRepository;
    private final JwtService jwtService;

    public void insertProducts(List<OrderProduct> orderProducts, HttpServletRequest request) throws ServletException, IOException {
        // 사용자 정보 추출
        String email = getUsername(request);

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        // 사용자 Cart에 주문상품 추가
        for (OrderProduct orderProduct : orderProducts) {
            member.addProductsInCart(orderProduct);
        }
    }

    public List<OrderProduct> getCart(HttpServletRequest request) throws ServletException, IOException {
        String email = getUsername(request);

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        Cart cart = member.getCart();
        return cart.getOrderProducts();
    }

    private String getUsername(HttpServletRequest request) throws ServletException, IOException {
        return jwtService.extractUsername(jwtService.extractAccessToken(request));
    }
}
