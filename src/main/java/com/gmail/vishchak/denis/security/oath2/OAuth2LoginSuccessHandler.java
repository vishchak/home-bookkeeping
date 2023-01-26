package com.gmail.vishchak.denis.security.oath2;

import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.service.CustomUserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final CustomUserServiceImpl userService;

    public OAuth2LoginSuccessHandler(CustomUserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken)authentication;
        OAuth2User user = token.getPrincipal();

        Map<String, Object> attributes = user.getAttributes();

        if (!userService.existsByEmail( (String) attributes.get("email"))) {
            userService.registerUser(new CustomUser(null, null,  (String) attributes.get("email"),  (String) attributes.get("picture")));
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }


}
