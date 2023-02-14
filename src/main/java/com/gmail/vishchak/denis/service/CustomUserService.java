package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.CustomUser;

public interface CustomUserService {

    boolean existsByLogin(String login);
    boolean existsByEmail(String email);

    CustomUser findUserByLoginOrEmail(String logMail);

    void registerUser(CustomUser user);

    void deleteUser(String mailLogin);
}
