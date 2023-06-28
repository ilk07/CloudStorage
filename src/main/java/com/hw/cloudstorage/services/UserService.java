package com.hw.cloudstorage.services;


import com.hw.cloudstorage.model.entity.User;

import java.security.Principal;

public interface UserService {
    User findByUsername(String username);
    User findById(Long id);

    User getUser(Principal principal);
}
