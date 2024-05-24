package study.myShop.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.myShop.domain.member.dto.MemberDefaultDto;
import study.myShop.domain.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final MemberService memberService;

    @GetMapping("/")
    public String index() {
        return "Hello World!";
    }

    @PostMapping("/join")
    public String register(@RequestBody MemberDefaultDto registerDto) {
        memberService.join(registerDto);

        return "success";
    }

    @GetMapping("/member/{id}")
    public ResponseEntity getMember(@PathVariable Long id) {
        return new ResponseEntity(memberService.getOne(id), HttpStatus.ACCEPTED);
    }
}
