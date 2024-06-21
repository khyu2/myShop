package study.myShop.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.entity.MemberStatus;
import study.myShop.domain.product.entity.Cart;

import java.time.LocalDateTime;

public record MemberDefaultDto(
        @Email String email,
        @Size(min = 4, max = 12) String password,
        @Size(min = 4, max = 12) String cPassword,
        String tel,
        String addr,
        String addrDetails
) {
    public Member toEntity() {
        return Member.builder().email(email).password(password).tel(tel).status(MemberStatus.USER)
                .addr(addr).addrDetails(addrDetails).createdAt(LocalDateTime.now()).build();
    }

    public boolean invalidPassword() {
        return !password.equals(cPassword);
    }
}
