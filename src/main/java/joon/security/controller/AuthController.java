package joon.security.controller;

import joon.security.dto.ResponseDTO;
import joon.security.dto.request.LoginFormDTO;
import joon.security.model.User;
import joon.security.service.AuthService;
import joon.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDTO> signUpUser(
            @RequestBody LoginFormDTO loginFormDTO
            ) {

        User user = authService.signUpUser(
                loginFormDTO.getEmail(),
                loginFormDTO.getPassword()
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDTO.builder()
                        .status(200)
                        .message("회원가입 성공")
                        .data(user)
                        .build()
        );
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDTO> loginUser(
            @RequestBody LoginFormDTO loginFormDTO,
            HttpServletResponse response
    ) {

        User user = authService.loginUser(
                loginFormDTO.getEmail(),
                loginFormDTO.getPassword()
        );

        Map<String, Cookie> cookies = authService.createCookie(user);
        response.addCookie(cookies.get(JwtUtil.ACCESS_TOKEN_NAME));
        response.addCookie(cookies.get(JwtUtil.REFRESH_TOKEN_NAME));

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDTO.builder()
                        .status(200)
                        .message("로그인 성공")
                        .data(user)
                        .build()
        );
    }

    @GetMapping("/user/test")
    public String userTest() {
        return "SUCCESS";
    }

    @GetMapping("/manager/test")
    public String managerTest() {
        return "SUCCESS";
    }

    @GetMapping("/admin/test")
    public String adminTest() {
        return "SUCCESS";
    }
}
