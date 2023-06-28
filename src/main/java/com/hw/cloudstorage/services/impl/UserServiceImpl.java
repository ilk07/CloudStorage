package com.hw.cloudstorage.services.impl;

import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.repositories.UserRepository;
import com.hw.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUser(Principal principal) {
        User user = findByUsername(principal.getName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found : " + principal.getName());
        }
        return user;
    }
}
