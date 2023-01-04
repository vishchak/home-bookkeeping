package com.gmail.vishchak.denis.security;

import com.gmail.vishchak.denis.views.list.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Collections;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/images/**").permitAll();

        super.configure(http);
        setLoginView(http, LoginView.class);
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new CrmInMemoryUserDetailsManager();
    }

    private static class CrmInMemoryUserDetailsManager extends InMemoryUserDetailsManager {
        public CrmInMemoryUserDetailsManager() {
            createUser(new User("user",
                    "{noop}pass",
                    Collections.singleton(new SimpleGrantedAuthority("USER"))));
        }
    }
}
