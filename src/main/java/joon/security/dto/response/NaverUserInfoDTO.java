package joon.security.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NaverUserInfoDTO {

    private String resultcode;
    private String message;
    private NaverSpecificUserInfoDTO response;
}