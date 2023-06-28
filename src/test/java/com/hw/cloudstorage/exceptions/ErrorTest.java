package com.hw.cloudstorage.exceptions;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ErrorTest {
    Error sut;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start Error Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---Error Class Test Completed---");
    }


    @BeforeEach
    void setUp() {
        sut = new Error("message", 1);
    }

    @Test
    @DisplayName("Error in test is Instance of Error")
    void Error() {
        assertInstanceOf(Error.class, sut);
    }

    @Test
    void getMessage() {
        String expected = "message";
        String actual = sut.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void getId() {
        int expected = 1;
        int actual = sut.getId();

        assertEquals(expected, actual);

    }
}