package study.myShop.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.myShop.domain.coupon.entity.Coupon;
import study.myShop.domain.order.entity.Order;
import study.myShop.domain.product.entity.Cart;
import study.myShop.domain.product.entity.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member")
    private final List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<Coupon> coupons = new ArrayList<>();

    // Member 객체에서 Cascade.ALL 을 해줬기 때문에 CartService에서 save 필요 x
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @Builder.Default
    private Cart cart = new Cart();

    @Column(nullable = false, unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private String tel;
    private String addr;
    private String addrDetails;

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    private String refreshToken;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        // Spring Security 특정 버전 이후부터는 {noop}을 접미사로 붙혀줘야 한다
        this.password = passwordEncoder.encode(this.password);
    }

    public boolean matchPassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password); // 순서 중요, rawPassword, encodedPassword
    }

    public void signOut() {
        this.status = MemberStatus.SIGNOUT;
    }

    /* 정보 수정(비밀번호, 전화번호, 주소, 상세주소) */
    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void addProductsInCart(Product product) {
        cart.setMember(this);
        cart.getProducts().add(product);
    }

    public void updateTel(String tel) {
        this.tel = tel;
    }

    public void updateAddr(String addr) {
        this.addr = addr;
    }

    public void updateAddrDetails(String addrDetails) {
        this.addrDetails = addrDetails;
    }

    public void updateRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public void destroyRefreshToken() { this.refreshToken = null; }

    public void updateLastModifiedAt() {
        this.lastModifiedAt = LocalDateTime.now();
    }
}
