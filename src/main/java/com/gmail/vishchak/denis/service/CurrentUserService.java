package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.CurrentUser;

public interface CurrentUserService {
    boolean existsByEmail(String email);

    boolean existsByLogin(String email);

    CurrentUser findUserByEmailOrLogin(String logMail);

    void registerUser(String login, String passwordHash);

    void deleteUser(String mailLogin);
}
