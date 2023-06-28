package com.hw.cloudstorage.model.entity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest extends BaseEntityTest{
    User sut;
    @Mock
    Role role;

    final static Long ID =1L;
    final static String MAIL = "test@mail";
    final static String PASSWORD = "password";
    final static String USERNAME = "user@name";

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start User Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---User Class Test Completed---");
    }

    @BeforeEach
    void setUp() {
        sut = new User();
        sut.setId(ID);
        sut.setEmail(MAIL);
        sut.setPassword(PASSWORD);
        sut.setRoles((List<Role>) role);
        sut.setUsername(USERNAME);
    }

    @Test
    void getUsername() {
        String expected = USERNAME;
        String actual = sut.getUsername();
        assertEquals(expected, actual);
    }

    @Test
    void getPassword() {
        String expected = PASSWORD;
        String actual = sut.getPassword();
        assertEquals(expected, actual);
    }

    @Test
    void getEmail() {
        String expected = MAIL;
        String actual = sut.getEmail();
        assertEquals(expected, actual);
    }

    @Test
    void getRoles() {
        List<Role> expected = (List<Role>) role;
        List<Role> actual = sut.getRoles();
        assertEquals(expected, actual);
    }

    @Test
    void setUsername() {
        String expected = USERNAME + USERNAME;
        sut.setUsername(expected);
        String actual = sut.getUsername();
        assertEquals(expected, actual);
    }

    @Test
    void setPassword() {
        String expected = PASSWORD + PASSWORD;
        sut.setPassword(expected);
        String actual = sut.getPassword();
        assertEquals(expected, actual);
    }

    @Test
    void setEmail() {
        String expected = MAIL + MAIL;
        sut.setEmail(expected);
        String actual = sut.getEmail();
        assertEquals(expected, actual);
    }
}