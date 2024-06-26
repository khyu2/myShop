package study.myShop.global.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;

import java.io.IOException;

/**
 * SimpleUrlAuthenticationSuccessHandler 는 강제로 redirect 하는 부분이 있어 위 인터페이스인
 * AuthenticationSuccessHandler 를 상속받은 JwtLoginSuccessHandler 를 사용
 */
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class JwtLoginSuccessProviderHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("JwtLoginSuccessProviderHandler 진입");
        String username = extractUsername(authentication);
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        log.info("response Status before {}", response.getStatus());

        clearAuthenticationAttributes(request);
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        memberRepository.findByEmail(username).ifPresent(
                member -> member.updateRefreshToken(refreshToken)
        );

        log.info("response Status {}", response.getStatus());
        log.info("{} 로그인에 성공합니다.", username);
        log.info("Access token: {}", accessToken);
        log.info("Refresh token: {}", refreshToken);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
