package com.hw.cloudstorage.controllers;

import com.hw.cloudstorage.model.AuthenticationToken;
import com.hw.cloudstorage.model.LoginCredentials;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.security.jwt.JwtTokenProvider;
import com.hw.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
@CrossOrigin("http://localhost:8080")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("login")
    public AuthenticationToken getAuthToken(@Valid @RequestBody LoginCredentials loginCredentials) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginCredentials.getLogin(), loginCredentials.getPassword())
            );
            User user = userService.findByUsername(loginCredentials.getLogin());
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            String token = jwtTokenProvider.createToken(user);

            return new AuthenticationToken(token);

        } catch (AuthenticationException ae) {
            throw new BadCredentialsException("Invalid login or password");
        }
    }

    @PostMapping("/logout")
    public void logout() {
    }
}
