package com.gmail.vishchak.denis.testdata;

import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner demo(final CurrentUserServiceImpl userService,
                                  final PasswordEncoder encoder) {
        return strings -> userService.registerUser("user",
                encoder.encode("pass"));
    }
}
