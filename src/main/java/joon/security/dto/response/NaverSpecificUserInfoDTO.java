package joon.security.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NaverSpecificUserInfoDTO {

    private String id;
    private String nickname;
    private String profile_image;
    private String age;
    private String gender;
    private String email;
    private String mobile;
    private String mobile_e164;
    private String name;
    private String birthday;
    private String birthyear;
}
