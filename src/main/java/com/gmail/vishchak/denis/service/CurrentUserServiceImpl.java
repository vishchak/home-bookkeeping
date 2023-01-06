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
    public boolean existsByEmail(String email) {
        return currentUserRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByLogin(String login) {
        return currentUserRepository.existsByLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUser findUserByEmailOrLogin(String logMail) {
        return currentUserRepository.findByEmailOrLogin(logMail);
    }


    @Override
    @Transactional
    public void registerUser(String login, String passwordHash) {
        if (existsByLogin(login)) {
            return;
        }

        CurrentUser user = new CurrentUser(login, passwordHash);
        currentUserRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String maleLogin) {
        CurrentUser user = findUserByEmailOrLogin(maleLogin);
        if (user == null) {
            return;
        }
        currentUserRepository.delete(user);
    }
}
