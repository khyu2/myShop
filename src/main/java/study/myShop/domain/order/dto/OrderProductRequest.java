package study.myShop.domain.order.dto;

import jakarta.validation.constraints.NotNull;

public record OrderProductRequest(
        @NotNull Long orderProductId, // 상품 번호
        @NotNull Long count, // 상품 수량
        @NotNull boolean couponCheck, // 쿠폰 사용 여부
        Long couponId) {
}
