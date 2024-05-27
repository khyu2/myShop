package study.myShop.domain.coupon.dto;

import jakarta.validation.constraints.NotNull;
import study.myShop.domain.coupon.entity.Coupon;
import study.myShop.domain.coupon.entity.CouponStatus;

public record CouponRequest(
        @NotNull String name,
        @NotNull String description,
        Long discountPrice,
        Long discountRate,
        Long memberId
        ) {

    /**
     * 사용 시 주의점: memberId 를 찾아서 setting 해주는 과정은 Service 단에서 처리
     */
    public Coupon toEntity() {
        return Coupon.builder().couponStatus(CouponStatus.ACT).description(description).name(name)
                .discountRate(discountRate).discountPrice(discountPrice).build();
    }
}
