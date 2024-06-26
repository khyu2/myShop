package study.myShop.domain.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.entity.MemberStatus;
import study.myShop.domain.member.repository.MemberRepository;

import java.io.IOException;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.junit.jupiter.api.Assertions.*;

// 참고 https://ttl-blog.tistory.com/272

@SpringBootTest
@Transactional
class JwtServiceTest {
    @Autowired JwtService jwtService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    private final String username = "ww@mail.com";

    @BeforeEach
    public void init(){
        Member member = Member.builder().email(username).password("1234567890").addr("Seoul").addrDetails("mapo-gu").status(MemberStatus.USER).build();
        memberRepository.save(member);
        clear();
    }

    private void clear(){
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token) {
        return JWT.require(HMAC512(secret)).build().verify(token);
    }

    @Test
    public void accessToken_발급() throws Exception {
        //given, when
        String accessToken = jwtService.createAccessToken(username);

        DecodedJWT verify = getVerify(accessToken);

        String subject = verify.getSubject();
        String findUsername = verify.getClaim(USERNAME_CLAIM).asString();

        //then
        assertEquals(username, findUsername);
        assertEquals(subject, ACCESS_TOKEN_SUBJECT);
    }

    @Test
    void refreshToken_발급() throws Exception {
        //given
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();
        String username = verify.getClaim(USERNAME_CLAIM).asString();

        //then
        assertEquals(subject, REFRESH_TOKEN_SUBJECT);
        assertNull(username);
    }

    @Test
    void refreshToken_update() throws Exception {
        //given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        Thread.sleep(3000);

        //when
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, reIssuedRefreshToken);
        clear();

        //then
        assertThrows(Exception.class, () -> memberRepository.findByRefreshToken(refreshToken).get());
        assertEquals(memberRepository.findByRefreshToken(reIssuedRefreshToken).get().getEmail(), username);
    }

    @Test
    void accessToken_헤더_설정() throws Exception {
        //given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);

        //when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        //then
        String header = mockHttpServletResponse.getHeader(accessHeader);
        assertEquals(header, accessToken);
    }

    @Test
    void refreshToken_헤더_설정() throws Exception {
        //given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String refreshToken = jwtService.createRefreshToken();

        //when
        jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);

        //then
        String refresh = mockHttpServletResponse.getHeader(refreshHeader);

        assertEquals(refresh, refreshToken);
    }

    @Test
    void username_추출() throws Exception {
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String requestAccessToken = jwtService.extractAccessToken(httpServletRequest)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        //when
        String extractUsername = jwtService.extractUsername(requestAccessToken)
                .orElseThrow(() -> new Exception("토큰이 없습니다."));

        //then
        assertEquals(extractUsername, username);
    }

    @Test
    void 토큰_유효성_검사() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        //when, then
        assertTrue(jwtService.isValid(accessToken));
        assertTrue(jwtService.isValid(refreshToken));
        assertFalse(jwtService.isValid(accessToken + "Hello"));
        assertFalse(jwtService.isValid(refreshToken + "Hello"));
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
        httpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);

        return httpServletRequest;
    }
}