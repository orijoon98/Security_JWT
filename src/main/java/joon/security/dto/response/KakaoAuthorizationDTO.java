package joon.security.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KakaoAuthorizationDTO {

    private String token_type;
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String refresh_token_expires_in;
    private String scope;
}