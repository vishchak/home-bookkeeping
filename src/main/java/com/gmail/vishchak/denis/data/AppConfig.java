package com.gmail.vishchak.denis.data;

import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.service.CustomUserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableScheduling
public class AppConfig {
    @Bean
    public CommandLineRunner demo(final CustomUserServiceImpl userService,
                                  final PasswordEncoder encoder) {
        return strings -> userService.registerUser(new CustomUser("user",
                encoder.encode("pass"), null, null));
    }
}
