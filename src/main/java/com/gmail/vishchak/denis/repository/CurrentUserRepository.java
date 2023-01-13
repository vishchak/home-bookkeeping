package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.CurrentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentUserRepository extends JpaRepository<CurrentUser, Long> {
    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @Query("select c from CurrentUser c where c.login = ?1 or c.email = ?1")
    CurrentUser findByLoginOrEmail(String login);
}
