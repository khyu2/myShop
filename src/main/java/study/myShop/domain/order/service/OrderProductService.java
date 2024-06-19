package study.myShop.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.coupon.entity.Coupon;
import study.myShop.domain.coupon.repoAndService.CouponRepository;
import study.myShop.domain.coupon.repoAndService.CouponService;
import study.myShop.domain.exception.CouponException;
import study.myShop.domain.exception.CouponExceptionType;
import study.myShop.domain.order.dto.OrderProductRequest;
import study.myShop.domain.order.entity.OrderProduct;
import study.myShop.domain.order.repository.OrderProductRepository;
import study.myShop.domain.product.entity.Product;
import study.myShop.domain.exception.ProductException;
import study.myShop.domain.exception.ProductExceptionType;
import study.myShop.domain.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;

    public List<OrderProduct> create(List<OrderProductRequest> orderProductRequestList) {
        List<OrderProduct> orderProductList = new ArrayList<>();

        // 주문 상품을 확인하며 상품이 존재하는지 확인 후 저장
        for (OrderProductRequest orderProductRequest : orderProductRequestList) {
            Product product = productRepository.findById(orderProductRequest.orderProductId())
                    .orElseThrow(
                            () -> new ProductException(ProductExceptionType.NOT_FOUND_PRODUCT)
                    );

            // 쿠폰 적용 여부 (쿠폰 아이디 존재 시 쿠폰 찾아서 적용)
            OrderProduct orderProduct;
            if (orderProductRequest.couponCheck()) {
                Coupon coupon = couponRepository.findById(orderProductRequest.couponId()).orElseThrow(
                        () -> new CouponException(CouponExceptionType.NOT_FOUND_COUPON)
                );

                // Entity 단에서 할인 적용 금액 계산하기보단 Service 단에서 처리하는게 좋음
                Long discountedPrice = CouponService.getDiscountedPrice(coupon, product.getPrice());
                orderProduct = OrderProduct.createOrderProduct(product, orderProductRequest.count(), discountedPrice);
            } else {
                orderProduct = OrderProduct.createOrderProduct(product, orderProductRequest.count());
            }
            orderProductList.add(orderProduct);
        }

        orderProductRepository.saveAll(orderProductList);
        return orderProductList;
    }
}
