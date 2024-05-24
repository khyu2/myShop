package study.myShop.global.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.entity.MemberDetails;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;

import java.io.IOException;

/**
 * 모든 서블릿 컨테이너에서 요청 디스패치당 단일 실행을 보장하는 것을 목표로 하는 필터 기본 클래스입니다.
 * 출처: https://ttl-blog.tistory.com/273#AccessToken을 통한 인증 Filter 추가-1 [Shin._.Mallang:티스토리]
 *
 * 설계 로직
 * 1. AccessToken O, RefreshToken O -> 로그인 성공, AccessToken 재발급
 * 1. AccessToken X, RefreshToken O -> 로그인 성공, AccessToken 재발급
 * 1. AccessToken O, RefreshToken X -> 로그인 성공, AccessToken 재발급 X
 * 1. AccessToken X, RefreshToken X -> 로그인 실패, 403 UnAuthorized
 */
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final String NO_CHECK_URL = "/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) { // login으로 오는 경우 필터 통과
            filterChain.doFilter(request, response);
            return;
        }

        // request 에서 refreshToken 추출
        String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isValid).orElse(null);

        // refreshToken 존재한다면 새 accessToken 발급
        if (refreshToken != null) {
            memberRepository.findByRefreshToken(refreshToken)
                    .ifPresent(member -> jwtService.sendAccessToken(response, jwtService.createAccessToken(member.getEmail())));
            return;
        }

//        refreshToken 존재하지 않으면 accessToken 대해서만 인증 진행
//        jwtService.extractAccessToken(request).filter(jwtService::isValid)
//                .ifPresent(accessToken -> jwtService.extractUsername(accessToken)
//                .ifPresent(email -> memberRepository.findByEmail(email)
//                .ifPresent(member -> saveAuthentication(member))));

        jwtService.extractAccessToken(request).filter(jwtService::isValid)
                .flatMap(accessToken -> jwtService.extractUsername(accessToken)
                .flatMap(memberRepository::findByEmail))
                .ifPresent(this::saveAuthentication);
    }

    private void saveAuthentication(Member member) {
        MemberDetails memberDetails = new MemberDetails(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                memberDetails, null, authoritiesMapper.mapAuthorities(memberDetails.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
