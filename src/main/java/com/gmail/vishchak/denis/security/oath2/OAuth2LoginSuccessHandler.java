package com.gmail.vishchak.denis.security.oath2;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final CurrentUserServiceImpl currentUserService;

    public OAuth2LoginSuccessHandler(CurrentUserServiceImpl currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getName();

        if (!currentUserService.existsByEmail(email)) {
            currentUserService.registerUser(new CurrentUser(null, null, email));
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }


}
