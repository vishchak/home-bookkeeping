package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.model.Image;
import com.gmail.vishchak.denis.model.enums.UserRole;

public interface CurrentUserService {
    boolean existsByEmail(String email);

    boolean existsByLogin(String email);

    CurrentUser findUserByEmailOrLogin(String logMail);

    void addUser(String login, String passwordHash, UserRole role, String email, Image image);

    void deleteUser(String mailLogin);
}
