package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.repository.CurrentUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    private final CurrentUserRepository currentUserRepository;

    public CurrentUserServiceImpl(CurrentUserRepository currentUserRepository) {
        this.currentUserRepository = currentUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByLogin(String login) {
        return currentUserRepository.existsByLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return currentUserRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUser findUserByLoginOrEmail(String login) {
        return currentUserRepository.findByLoginOrEmail(login);
    }


    @Override
    @Transactional
    public void registerUser(CurrentUser user) {
        if (user.getLogin() != null) {
            if (!user.getLogin().isEmpty() && !user.getLogin().isBlank()) {
                if (!existsByLogin(user.getLogin())) {
                    currentUserRepository.save(user);
                }
            }

        } else if (user.getEmail() != null) {
            if (!user.getEmail().isEmpty() && !user.getEmail().isBlank()) {
                if (!existsByEmail(user.getEmail())) {
                    currentUserRepository.save(user);
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(String login) {
        if (existsByLogin(login)) {
            currentUserRepository.delete(findUserByLoginOrEmail(login));
        }
    }
}
