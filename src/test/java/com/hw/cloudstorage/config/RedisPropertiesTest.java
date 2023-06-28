package com.hw.cloudstorage.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisPropertiesTest {

    RedisProperties sut;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start JwtTokenProperties Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---JwtTokenProperties Class Test Completed---");
    }

    @BeforeEach
    void setUp() {
        sut = new RedisProperties();
        sut.setHost("redis-host");
        sut.setPort(0000);
    }

    @Test
    void getHost() {
        String expected = "redis-host";
        String actual = sut.getHost();

        assertEquals(expected, actual);
    }

    @Test
    void getPort() {
        int expected = 0000;
        int actual = sut.getPort();

        assertEquals(expected, actual);
    }

    @Test
    void setHost() {
        String expected = "set-host";
        sut.setHost(expected);
        String actual = sut.getHost();

        assertEquals(expected, actual);
    }

    @Test
    void setPort() {
        int expected = 1111;
        sut.setPort(expected);
        int actual = sut.getPort();

        assertEquals(expected, actual);
    }
}