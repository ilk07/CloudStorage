package com.hw.cloudstorage.model.entity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockedTokenTest {

    private final Long expiration = 30L;
    private final String token = "some.user.token";
    BlockedToken sut;


    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start BlockedToken Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---BlockedToken Class Test Completed---");
    }

    @BeforeEach
    void initOneTest(){
        sut = new BlockedToken(token, expiration);
    }

    @Test
    void getToken() {
        String expected = token;
        String actual = sut.getToken();

        assertEquals(expected, actual);

    }

    @Test
    void getExpiration() {
        Long expected = expiration;
        Long actual = sut.getExpiration();

        assertEquals(expected, actual);
    }

    @Test
    void setToken() {
        String expected = token + token;
        sut.setToken(expected);
        String actual = sut.getToken();

        assertEquals(expected, actual);
    }

    @Test
    void setExpiration() {
        Long expected = expiration + expiration;
        sut.setExpiration(expected);
        Long actual = sut.getExpiration();

        assertEquals(expected, actual);
    }
}