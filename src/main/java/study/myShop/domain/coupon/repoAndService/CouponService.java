package study.myShop.domain.coupon.repoAndService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.myShop.domain.coupon.dto.CouponRequest;
import study.myShop.domain.coupon.entity.Coupon;
import study.myShop.domain.exception.ProductException;
import study.myShop.domain.exception.ProductExceptionType;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.exception.MemberException;
import study.myShop.domain.exception.MemberExceptionType;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.order.entity.Order;

/**
 * 기능
 * 1. 쿠폰 발급 (사용자 지정)
 * 2. 쿠폰 사용
 * 3. 쿠폰 사용 취소 (Order Cancel 시)
 */
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    public Long issue(CouponRequest couponRequest) {
        Member member = memberRepository.findById(couponRequest.memberId()).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        Coupon coupon = couponRequest.toEntity();
        coupon.addMember(member);

        return couponRepository.save(coupon).getId();
    }

    public static Long getDiscountedPrice(Coupon coupon, Long productPrice) {
        if (coupon.getDiscountPrice() != null) { // 고정 금액 할인 적용
            long price = productPrice - coupon.getDiscountPrice();
            if (price < 0) {
                throw new ProductException(ProductExceptionType.INCORRECT_DISCOUNT_APPLIED);
            }
            return price;
        } else {
            long discount = productPrice * coupon.getDiscountRate();
            long price = productPrice - discount;
            if (price < 0) {
                throw new ProductException(ProductExceptionType.INCORRECT_DISCOUNT_APPLIED);
            }
            return price;
        }
    }
}
