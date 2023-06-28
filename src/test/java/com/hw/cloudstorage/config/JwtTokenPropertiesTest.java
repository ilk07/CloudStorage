package com.hw.cloudstorage.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class JwtTokenPropertiesTest {

    JwtTokenProperties sut;

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
        sut = new JwtTokenProperties();
        sut.setBearer("test-bearer");
        sut.setExpired(30L);
        sut.setHeader("test-header");
        sut.setSecret("test-secret-phrase");
        sut.setIssuer("test-issuer");
    }

    @Test
    void JwtTokenProperties() {
        assertInstanceOf(JwtTokenProperties.class, sut);
    }

    @Test
    void getSecret() {
        String expected = "test-secret-phrase";
        String actual = sut.getSecret();

        assertEquals(expected, actual);
    }

    @Test
    void getExpired() {
        Long expected = 30L;
        Long actual = sut.getExpired();

        assertEquals(expected, actual);
    }

    @Test
    void getHeader() {
        String expected = "test-header";
        String actual = sut.getHeader();

        assertEquals(expected, actual);
    }

    @Test
    void getBearer() {
        String expected = "test-bearer";
        String actual = sut.getBearer();

        assertEquals(expected, actual);
    }

    @Test
    void getIssuer() {
        String expected = "test-issuer";
        String actual = sut.getIssuer();

        assertEquals(expected, actual);
    }

    @Test
    void setSecret() {
        String expected = "new secret";
        sut.setSecret(expected);
        String actual = sut.getSecret();

        assertEquals(expected, actual);
    }

    @Test
    void setExpired() {
        Long expected = 10L;
        sut.setExpired(expected);
        Long actual = sut.getExpired();

        assertEquals(expected, actual);
    }

    @Test
    void setHeader() {
        String expected = "new header";
        sut.setHeader(expected);
        String actual = sut.getHeader();

        assertEquals(expected, actual);
    }

    @Test
    void setBearer() {
        String expected = "new bearer";
        sut.setBearer(expected);
        String actual = sut.getBearer();

        assertEquals(expected, actual);
    }

    @Test
    void setIssuer() {
        String expected = "new issuer";
        sut.setIssuer(expected);
        String actual = sut.getIssuer();

        assertEquals(expected, actual);
    }
}