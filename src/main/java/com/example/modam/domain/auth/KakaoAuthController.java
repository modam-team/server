package com.example.modam.domain.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Map;

@RestController
public class KakaoAuthController {
    @GetMapping("/login/oauth2/code/kakao")
    public String kakaoLoginSuccess(@AuthenticationPrincipal OAuth2User oauth2User){
        Map<String, Object> attributes = oauth2User.getAttributes();

        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (String) properties.get("nickname");
        String profileImage = (String) properties.get("profile_image");

        // 콘솔 출력
        System.out.println("카카오 로그인 성공 정보");
        System.out.println("ID: " + attributes.get("id"));
        System.out.println("닉네임: " + nickname);
        System.out.println("프로필 사진 URL: " + profileImage);

        // 테스트 결과를 브라우저에 출력
        return "<h1>카카오 로그인 성공!</h1>"
             + "<h2>환영합니다, " + nickname + "님!</h2>"
             + "<p>받은 전체 정보: " + attributes + "</p>";

    }
}
