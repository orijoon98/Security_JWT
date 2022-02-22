package joon.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import joon.security.constant.Role;
import joon.security.dto.response.GoogleUserInfoDTO;
import joon.security.dto.response.KakaoAuthorizationDTO;
import joon.security.dto.response.KakaoUserInfoDTO;
import joon.security.exception.CodeException;
import joon.security.exception.IdTokenException;
import joon.security.model.User;
import joon.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUrl;

    @Value("${kakao.authorization-grant-type}")
    private String kakaoGrantType;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    public User loginKakaoUser(String code) {
        KakaoAuthorizationDTO authorization = getKakaoAuthorization(code);
        KakaoUserInfoDTO userInfoFromKakao = getUserInfoByAccessToken(authorization.getAccess_token());

        Optional<User> user = userRepository.findByEmail(userInfoFromKakao.getId().toString());
        if(user.isPresent()) {
            return user.get();
        }

        User newUser = User.builder()
                .email(userInfoFromKakao.getId().toString())
                .password(passwordEncoder.encode(""))
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(newUser);
    }

    public User loginGoogleUser(String idToken) {
        GoogleUserInfoDTO userInfoFromGoogle = getUserInfoByIdToken(idToken);

        Optional<User> user = userRepository.findByEmail(userInfoFromGoogle.getEmail());
        if(user.isPresent()) {
            return user.get();
        }

        User newUser = User.builder()
                .email(userInfoFromGoogle.getEmail())
                .password(passwordEncoder.encode(""))
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(newUser);
    }

    private KakaoAuthorizationDTO getKakaoAuthorization(String code) {
        final String kakaoTokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", kakaoGrantType);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUrl);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoTokenUri, request, String.class);

            KakaoAuthorizationDTO authorization = objectMapper.readValue(response.getBody(), KakaoAuthorizationDTO.class);

            return authorization;
        } catch (RestClientException | JsonProcessingException ex) {

            throw new RuntimeException(ex.getMessage());
        }
    }

    private KakaoUserInfoDTO getUserInfoByAccessToken(String accessToken) {
        final String kakaoUserInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoUserInfoUri, request, String.class);
            KakaoUserInfoDTO userInfoDto = objectMapper.readValue(response.getBody(), KakaoUserInfoDTO.class);

            return userInfoDto;
        } catch (RestClientException | JsonProcessingException ex) {

            throw new CodeException();
        }
    }

    public GoogleUserInfoDTO getUserInfoByIdToken(String idToken) {
        final String tokenInfoUri = "https://oauth2.googleapis.com/tokeninfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id_token", idToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenInfoUri, request, String.class);
            GoogleUserInfoDTO userInfoDto = objectMapper.readValue(response.getBody(), GoogleUserInfoDTO.class);

            return userInfoDto;
        } catch (RestClientException | JsonProcessingException ex) {

            throw new IdTokenException();
        }
    }
}
