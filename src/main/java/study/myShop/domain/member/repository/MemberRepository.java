package study.myShop.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.myShop.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);

    boolean existsByEmail(String email);
    boolean existsByTel(String tel);
}
