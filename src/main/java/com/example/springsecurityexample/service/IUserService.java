package com.example.springsecurityexample.service;

import com.example.springsecurityexample.validation.EmailExistsException;
import com.example.springsecurityexample.web.model.User;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

    User updateExistingUser(User user) throws EmailExistsException;

}
