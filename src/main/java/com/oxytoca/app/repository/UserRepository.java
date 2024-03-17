package com.oxytoca.app.repository;

import com.oxytoca.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findUserById(Long id);

    User findByActivationCode(String code);

}
