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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import study.myShop.domain.member.entity.Member;
import study.myShop.domain.member.entity.MemberStatus;
import study.myShop.domain.member.exception.MemberException;
import study.myShop.domain.member.exception.MemberExceptionType;
import study.myShop.domain.member.repository.MemberRepository;
import study.myShop.domain.member.service.JwtService;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String KEY_USERNAME = "email";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "ww@mail";
    private static String PASSWORD = "123456789";

    private static String LOGIN_RUL = "/login";

    void clear() {
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void init(){
        Member member = Member.builder().email(USERNAME).password(passwordEncoder.encode(PASSWORD)).addr("Seoul").addrDetails("mapo-gu").status(MemberStatus.USER).build();
        memberRepository.save(member);
        clear();
    }

    private Map getUsernamePasswordMap(String username, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }

    @Test
    void 로그인_성공_accessToken_refreshToken_발급성공() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        //then
        MvcResult mvcResult = mockMvc.perform(
                post(LOGIN_RUL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map))
        ).andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String accessToken = response.getHeader(accessHeader);
        String refreshToken = response.getHeader(refreshHeader);

        String email = jwtService.extractUsername(accessToken).orElseThrow(
                () -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        );

        assertEquals(USERNAME, email);
        assertTrue(jwtService.isValid(refreshToken));
    }

    @Test
    void 로그인_실패_아이디오류() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME + "###", PASSWORD);

        //then
        mockMvc.perform(
                post(LOGIN_RUL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map))
        ).andExpect(status().isForbidden());
    }
    @Test
    void 로그인_실패_비밀번호오류() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD + "###");

        //then
        mockMvc.perform(
                post(LOGIN_RUL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map))
        ).andExpect(status().isForbidden());
    }

    @Test
    void 로그인_형식_확인() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        //then
        mockMvc.perform(
                post(LOGIN_RUL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map))
        ).andExpect(status().isForbidden());
    }
}
