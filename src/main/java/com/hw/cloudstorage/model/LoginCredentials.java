package com.hw.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginCredentials {
    @NotEmpty(message = "login is empty")
    private String login;
    @NotEmpty(message = "password is empty")
    private String password;
}
