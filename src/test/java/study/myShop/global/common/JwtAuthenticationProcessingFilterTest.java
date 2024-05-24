package study.myShop.global.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.entity.MemberStatus;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtAuthenticationProcessingFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String KEY_USERNAME = "email";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "ww@mail";
    private static String PASSWORD = "123456789";

    private static String LOGIN_RUL = "/login";

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer ";

    ObjectMapper objectMapper = new ObjectMapper();

    public void clear() {
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void init(){
        Member member = Member.builder().email(USERNAME).password("{noop}" + passwordEncoder.encode(PASSWORD)).addr("Seoul").addrDetails("mapo-gu").status(MemberStatus.USER).build();
        memberRepository.save(member);
        clear();
    }

    private Map getUsernamePasswordMap(String username, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }


    private Map getAccessAndRefreshToken() throws Exception {
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(
                        post(LOGIN_RUL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader,accessToken);
        tokenMap.put(refreshHeader,refreshToken);

        return tokenMap;
    }

    // AccessToken X, RefreshToken X
    @Test
    void acX_reX() throws Exception {
        // 인가받지 않은 로그인 요청 시, AuthenticationEntryPoint 로 진입한다
        mockMvc.perform(
                get(LOGIN_RUL + "123")
        ).andExpect(status().isForbidden());
    }

    @Test
    void ac_notValid_reX() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);

        //then
        mockMvc.perform(
                get(LOGIN_RUL).header(accessHeader, BEARER + accessToken)
        ).andExpect(status().isOk());
    }
}