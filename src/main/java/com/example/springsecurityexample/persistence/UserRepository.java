package com.example.springsecurityexample.persistence;

import com.example.springsecurityexample.web.model.User;

public interface UserRepository {

    Iterable<User> findAll();

    User save(User user);

    User findUser(Long id);

    void deleteUser(Long id);

}
