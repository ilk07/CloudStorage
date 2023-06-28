package com.hw.cloudstorage.dto;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileNameDtoTest {

    FileNameDto sut;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start FileNameDto Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---FileNameDto Class Test Completed---");
    }


    @BeforeEach
    void setUp() {
        sut = new FileNameDto("updateFileName");
    }

    @Test
    void getFilename() {
        String expected = "updateFileName";
        String actual = sut.getFilename();

        assertEquals(expected, actual);
    }

    @Test
    void setFilename() {
        String expected = "newUpdateFileName";
        sut.setFilename(expected);
        String actual = sut.getFilename();

        assertEquals(expected, actual);
    }
}