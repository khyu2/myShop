package study.myShop.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.myShop.domain.order.entity.Order;

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
        this.password = passwordEncoder.encode(this.password);
    }

    public void signOut() {
        this.status = MemberStatus.SIGNOUT;
    }

    /* 정보 수정(비밀번호, 전화번호, 주소, 상세주소) */
    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
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
