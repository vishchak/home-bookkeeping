package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.CurrentUser;

public interface CurrentUserService {

    boolean existsByLogin(String login);
    boolean existsByEmail(String email);

    CurrentUser findUserByLoginOrEmail(String logMail);

    void registerUser(CurrentUser user);

    void deleteUser(String mailLogin);
}
