package com.hw.cloudstorage.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginCredentialsTest {

    private LoginCredentials sut;
    private final static String login = "test-login";
    private final static String password = "test-password";

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start LoginCredentials Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---LoginCredentials Class Test Completed---");
    }

    @BeforeEach
    void setUp() {
        sut = new LoginCredentials(login, password);
    }

    @Test
    void getLogin() {
        String expected = login;
        String actual = sut.getLogin();
        assertEquals(expected, actual);

    }

    @Test
    void getPassword() {
        String expected = password;
        String actual = sut.getPassword();
        assertEquals(expected, actual);
    }

    @Test
    void setLogin() {
        String expected = login + login;
        sut.setLogin(expected);
        String actual = sut.getLogin();
        assertEquals(expected, actual);
    }

    @Test
    void setPassword() {
        String expected = password + password;
        sut.setPassword(expected);
        String actual = sut.getPassword();
        assertEquals(expected, actual);
    }
}