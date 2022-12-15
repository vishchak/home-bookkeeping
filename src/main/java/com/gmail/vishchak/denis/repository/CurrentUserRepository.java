package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.CurrentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentUserRepository extends JpaRepository<CurrentUser, Long> {
    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    @Query("select c from CurrentUser c where c.email = ?1 or c.login = ?1")
    CurrentUser findByEmailOrLogin(String logMail);
}
