package joon.security.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import joon.security.constant.Role;
import joon.security.dto.ErrorDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(403);
        response.setContentType("application/json;charset=utf-8");
        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(403)
                .message("접근 가능한 권한을 가지고 있지 않습니다.")
                .build();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser user = (SecurityUser)authentication.getPrincipal();
        Collection<GrantedAuthority> authorities = user.getAuthorities();

        if (hasRole(authorities, Role.ROLE_NOT_PERMITTED.name())) {
            errorDTO.setMessage("사용자 인증이 완료되지 않았습니다.");
        }

        PrintWriter out = response.getWriter();
        String jsonResponse = objectMapper.writeValueAsString(errorDTO);
        out.print(jsonResponse);
    }

    private boolean hasRole(Collection<GrantedAuthority> authorites, String role){
        return authorites.contains(new SimpleGrantedAuthority(role));
    }
}