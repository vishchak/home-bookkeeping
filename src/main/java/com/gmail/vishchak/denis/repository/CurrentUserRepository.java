package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.CurrentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CurrentUserRepository extends JpaRepository<CurrentUser, Long> {
    boolean existsByEmail(String email);

    @Query("select c from CurrentUser c where c.email = ?1 or c.login = ?2")
    CurrentUser findByEmailOrLogin(String email, String login);
}
