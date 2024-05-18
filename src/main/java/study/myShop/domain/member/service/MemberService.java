package study.myShop.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.dto.MemberDefaultDto;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.exception.MemberException;
import study.myShop.domain.member.exception.MemberExceptionType;
import study.myShop.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long join(MemberDefaultDto memberDto) {
        if (memberRepository.existsByEmail(memberDto.email())) {
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_USERNAME);
        }

        if (memberRepository.existsByTel(memberDto.tel())) {
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_TEL);
        }

        Member member = memberDto.toEntity();

        member.encodePassword(passwordEncoder);

        return memberRepository.save(member).getId();
    }

    /**
     * MemberStatus: USER -> SIGNOUT
     * 판매자가 등록한 물품 내리기
     */
    public void signOut(MemberDefaultDto memberDto) {
        if (memberDto.invalidPassword()) {
            throw new MemberException(MemberExceptionType.PASSWORD_NOT_EQUAL);
        }

        Member member = memberRepository.findByEmail(memberDto.email()).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        if (member.getPassword().equals(passwordEncoder.encode(memberDto.password()))) {
            member.signOut();
        }
    }

    public void update(MemberDefaultDto memberDto) {
        if (memberDto.invalidPassword()) {
            throw new MemberException(MemberExceptionType.PASSWORD_NOT_EQUAL);
        }

        Member member = memberRepository.findByEmail(memberDto.email()).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        member.updatePassword(passwordEncoder, memberDto.password());
        member.updateTel(memberDto.tel());
        member.updateAddr(memberDto.addr());
        member.updateAddr(memberDto.addr());
        member.updateAddrDetails(memberDto.addrDetails());
        member.updateLastModifiedAt();
    }

    @Transactional(readOnly = true)
    public Member getOne(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );
    }
}
