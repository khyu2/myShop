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
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.repository.OrderProductRepository;
import study.myShop.domain.order.service.OrderProductService;
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
    private final OrderProductService orderProductService;
    private final CartRepository cartRepository;
    private final JwtService jwtService;

    // orderProducts로 받으면 안되고 OrderProductRequest를 통해 입력받아야함 -> OrderProductService 사용.
    @Transactional
    public void insertProducts(List<OrderProductRequest> orderProductRequests, HttpServletRequest request) throws ServletException, IOException {
        // 사용자 정보 추출
        String email = getUsername(request);

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        List<OrderProduct> orderProducts = orderProductService.create(orderProductRequests);

        // 사용자 Cart에 주문상품 추가
        for (OrderProduct orderProduct : orderProducts) {
            member.addProductsInCart(orderProduct);
        }

        if (!cartRepository.existsByMemberId(member.getId())) {
            cartRepository.save(member.getCart());
        }
    }

    /**
     * 제거할 상품 목록
     * 사용자 장바구니 꺼내와서 List에서 제거
     */
    @Transactional
    public void removeProducts(List<OrderProduct> removeProducts, HttpServletRequest request) throws ServletException, IOException {
        String email = getUsername(request);

        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        Cart cart = member.getCart();
        List<OrderProduct> orderProducts = cart.getOrderProducts();

        // 장바구니 순회하며 removeProducts 에 포함된 상품들 제거
        // -> 만약 수량을 2개 -> 1개로 줄이는 거면 어떻게 처리하지? -> update로 따로 관리
        // OrderProduct를 ArrayList로 관리할 필요가 있나? HashMap을 통한 삽입, 삭제가 더 효율적
        // 로직: 각 주문상품을 순회하며 삭제할 상품과 '이름'이 같다면 제거한다 (수량 x)
        orderProducts.removeIf(orderProduct ->
                removeProducts.stream().anyMatch(remove -> remove.getProduct().getName().equals(orderProduct.getProduct().getName())));
    }

    public List<OrderProduct> getWishes(HttpServletRequest request) throws ServletException, IOException {
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
