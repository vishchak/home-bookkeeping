package com.gmail.vishchak.denis.security.oath2;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
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
    private final CurrentUserServiceImpl currentUserService;

    public OAuth2LoginSuccessHandler(CurrentUserServiceImpl currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken)authentication;
        OAuth2User user = token.getPrincipal();

        Map<String, Object> attributes = user.getAttributes();

        if (!currentUserService.existsByEmail( (String) attributes.get("email"))) {
            currentUserService.registerUser(new CurrentUser(null, null,  (String) attributes.get("email"),  (String) attributes.get("picture")));
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }


}
