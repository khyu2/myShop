package study.myShop.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.entity.MemberDetails;
import study.myShop.domain.member.entity.MemberStatus;
import study.myShop.domain.member.repository.MemberRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(username + "-> 사용자를 찾을 수 없습니다.")
        );

        log.info("DB에서 가져온 유저정보 username = {}, password = {}", member.getEmail(), member.getPassword());
        return new MemberDetails(member);
    }
}
