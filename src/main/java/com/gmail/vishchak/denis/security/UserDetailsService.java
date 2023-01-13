package com.gmail.vishchak.denis.security;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    private final CurrentUserServiceImpl currentUserService;
    public UserDetailsService(CurrentUserServiceImpl currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CurrentUser user = currentUserService.findUserByLoginOrEmail(username);
        if (user == null)
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username));

        List<GrantedAuthority> roles = List.of(
                new SimpleGrantedAuthority(user.getRole().toString())
        );

        return new User(user.getLogin(), user.getPasswordHash(), roles);
    }
}
