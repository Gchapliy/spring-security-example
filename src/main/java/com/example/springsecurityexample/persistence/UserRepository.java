package com.example.springsecurityexample.persistence;

import com.example.springsecurityexample.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
