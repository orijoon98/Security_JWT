package joon.security.controller;

import joon.security.dto.ResponseDTO;
import joon.security.dto.request.GoogleLoginDTO;
import joon.security.dto.request.KakaoLoginDTO;
import joon.security.model.User;
import joon.security.service.AuthService;
import joon.security.service.OauthService;
import joon.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OauthController {

    private final OauthService oauthService;
    private final AuthService authService;

    @PostMapping("/kakao")
    public ResponseEntity<ResponseDTO> loginKakaoUser(
            @RequestBody KakaoLoginDTO kakaoLoginDTO,
            HttpServletResponse response
    ) {
        User user = oauthService.loginKakaoUser(kakaoLoginDTO.getCode());

        Map<String, Cookie> cookies = authService.createCookie(user);
        response.addCookie(cookies.get(JwtUtil.ACCESS_TOKEN_NAME));
        response.addCookie(cookies.get(JwtUtil.REFRESH_TOKEN_NAME));

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDTO.builder()
                        .status(200)
                        .message("카카오 로그인 성공")
                        .data(user)
                        .build()
        );
    }

    @PostMapping("/google")
    public ResponseEntity<ResponseDTO> loginGoogleUser(
            @RequestBody GoogleLoginDTO googleLoginDTO,
            HttpServletResponse response
    ) {
        String userInfo = oauthService.loginGoogleUser(googleLoginDTO.getIdToken());

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDTO.builder()
                        .status(200)
                        .message("구글 로그인 성공")
                        .data(userInfo)
                        .build()
        );
    }
}
