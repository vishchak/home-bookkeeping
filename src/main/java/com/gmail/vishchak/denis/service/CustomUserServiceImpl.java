package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.repository.CurrentUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserServiceImpl implements CustomUserService {
    private final CurrentUserRepository userRepository;

    public CustomUserServiceImpl(CurrentUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomUser findUserByLoginOrEmail(String login) {
        return userRepository.findByLoginOrEmail(login);
    }


    @Override
    @Transactional
    public void registerUser(CustomUser user) {
        if (user.getLogin() != null) {
            if (!user.getLogin().isEmpty() && !user.getLogin().isBlank()) {
                if (!existsByLogin(user.getLogin())) {
                    userRepository.save(user);
                }
            }

        } else if (user.getEmail() != null) {
            if (!user.getEmail().isEmpty() && !user.getEmail().isBlank()) {
                if (!existsByEmail(user.getEmail())) {
                    userRepository.save(user);
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(String login) {
        if (existsByLogin(login)) {
            userRepository.delete(findUserByLoginOrEmail(login));
        }
    }
}
