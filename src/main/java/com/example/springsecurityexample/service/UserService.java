package com.example.springsecurityexample.service;

import javax.transaction.Transactional;

import com.example.springsecurityexample.web.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springsecurityexample.persistence.UserRepository;
import com.example.springsecurityexample.validation.EmailExistsException;
import com.example.springsecurityexample.web.model.User;

@Service
@Transactional
class UserService implements IUserService {

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    @Override
    public User registerNewUser(final User user) throws EmailExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return repository.save(user);
    }

    private boolean emailExist(String email) {
        final User user = repository.findByEmail(email);
        return user != null;
    }

    @Override
    public User updateExistingUser(User user) throws EmailExistsException {
        final Long id = user.getId();
        final String email = user.getEmail();
        final User emailOwner = repository.findByEmail(email);
        if (emailOwner != null && !id.equals(emailOwner.getId())) {
            throw new EmailExistsException("Email not available.");
        }
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return repository.save(user);
    }

    @Override
    public void createVerificationTokenForUser(User user, String token) {

    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return null;
    }

    @Override
    public void saveRegisteredUser(User user) {
        repository.save(user);
    }

}
