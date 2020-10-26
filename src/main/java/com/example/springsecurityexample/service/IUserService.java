package com.example.springsecurityexample.service;

import com.example.springsecurityexample.validation.EmailExistsException;
import com.example.springsecurityexample.web.model.User;
import com.example.springsecurityexample.web.model.VerificationToken;

public interface IUserService {

    User findUserByEmail(final String email);

    User registerNewUser(User user) throws EmailExistsException;

    User updateExistingUser(User user) throws EmailExistsException;

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String token);

    void saveRegisteredUser(User user);

}
