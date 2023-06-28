package com.hw.cloudstorage.model.enums;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorTypeTest {
    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start ErrorType Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---ErrorType Class Test Completed---");
    }

    @Test
    void getErrorId() {
        int expected = 101;
        int actual = ErrorType.LOGIN_CREDENTIALS.getErrorId();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @EnumSource(ErrorType.class)
    void getErrorId_IsAlwaysMoreThan100(ErrorType errorType) {
        int actual = errorType.getErrorId();
        assertTrue(actual > 100);
    }

}