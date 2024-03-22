package com.oxytoca.app.repository;

import com.oxytoca.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс для выполнения операций с объектами типа Activity
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findUserById(Long id);

    User findByActivationCode(String code);

}
