package com.gmail.vishchak.denis.security;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.security.oath2.CustomOAuth2User;
import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;


@Component
public class SecurityService {
    private final CurrentUserServiceImpl currentUserService;

    public SecurityService(CurrentUserServiceImpl currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public CurrentUser getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();

        if (principal instanceof UserDetails || principal instanceof CustomOAuth2User) {
            return currentUserService.findUserByLoginOrEmail(context.getAuthentication().getName());
        }
        // Anonymous or no authentication.
        return null;
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation("/");
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}
