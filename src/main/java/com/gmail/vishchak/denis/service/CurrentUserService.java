package com.gmail.vishchak.denis.service;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.model.Image;
import org.apache.catalina.User;

public interface CurrentUserService {
    boolean existsByEmail(String email);

    boolean existsByLogin(String email);

    CurrentUser findUserByEmailOrLogin(String logMail);

    boolean addUser(String login, String passwordHash, String email, Image image);

    void deleteUser(String mailLogin);
}
