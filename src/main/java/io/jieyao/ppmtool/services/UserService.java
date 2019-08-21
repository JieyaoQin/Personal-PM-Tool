package io.jieyao.ppmtool.services;

import io.jieyao.ppmtool.domain.User;
import io.jieyao.ppmtool.exceptions.UserNameAlreadyExistsException;
import io.jieyao.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(User newUser) {
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            // username has to be unique
            newUser.setUsername(newUser.getUsername());
            // make sure that password and confirm password match
            // make sure we don't persist confirm password
            return userRepository.save(newUser);
        } catch(Exception e) {
            throw new UserNameAlreadyExistsException("Username " + newUser.getUsername() + " already exists");
        }
    }
}
