package com.gmail.vishchak.denis.repository;

import com.gmail.vishchak.denis.model.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentUserRepository extends JpaRepository<CustomUser, Long> {
    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @Query("select c from CustomUser c where c.login = ?1 or c.email = ?1")
    CustomUser findByLoginOrEmail(String login);
}
