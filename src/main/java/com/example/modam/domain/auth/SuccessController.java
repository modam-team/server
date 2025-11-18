package com.example.modam.domain.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SuccessController {
    @GetMapping("/oauth/loginInfo")
    public String kakaoLoginSuccess(@AuthenticationPrincipal OAuth2User oauth2User){
        return "<h1> 카카오 로그인 성긍!</h1>"
                + "<h2>받은 정보: " + oauth2User.getAttributes().toString() + "</h2>";
    }
}
