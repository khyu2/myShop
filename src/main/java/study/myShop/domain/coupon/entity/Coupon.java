package study.myShop.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.exception.ProductException;
import study.myShop.domain.exception.ProductExceptionType;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;
    private Long discountRate;
    private Long discountPrice;

    private String description;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    public void addMember(Member member) {
        this.member = member;
        member.getCoupons().add(this);
    }
}
