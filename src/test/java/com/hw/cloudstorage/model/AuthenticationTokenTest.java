package com.hw.cloudstorage.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationTokenTest {

    private AuthenticationToken sut;
    private final static String TOKEN = "some.auth.token.for.test";

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start AuthenticationToken Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---AuthenticationToken Class Test Completed---");
    }

    @BeforeEach
    void setUp() {
        sut = new AuthenticationToken();
        sut.setAuthToken(TOKEN);
    }

    @Test
    void getAuthToken() {
        String expected = TOKEN;
        String actual = sut.getAuthToken();
        assertEquals(expected, actual);
    }

    @Test
    void setAuthToken() {
        String expected = TOKEN + TOKEN;
        sut.setAuthToken(expected);
        String actual = sut.getAuthToken();
        assertEquals(expected, actual);
    }

    @Test
    void getAuthTokenAsJsonFieldName() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String actual = mapper.writeValueAsString(sut);
        assertTrue(actual.contains("auth-token"));

    }


}