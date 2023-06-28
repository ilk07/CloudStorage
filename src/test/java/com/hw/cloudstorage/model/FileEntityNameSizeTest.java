package com.hw.cloudstorage.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileEntityNameSizeTest {

    private FileEntityNameSize sut;
    private final static String fileName = "test-name";
    private final static Long fileSize = 50L;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start FileEntityNameSize Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---FileEntityNameSize Class Test Completed---");
    }

    @BeforeEach
    void setUp() {
        sut = new FileEntityNameSize(fileName,fileSize);
    }

    @Test
    void getFilename() {
        String expected = fileName;
        String actual = sut.getFilename();
        assertEquals(expected, actual);
    }

    @Test
    void getSize() {
        Long expected = fileSize;
        Long actual = sut.getSize();
        assertEquals(expected, actual);
    }

    @Test
    void setFilename() {
        String expected = fileName + fileName;
        sut.setFilename(expected);
        String actual = sut.getFilename();
        assertEquals(expected, actual);
    }

    @Test
    void setSize() {
        Long expected = fileSize + fileSize;
        sut.setSize(expected);
        Long actual = sut.getSize();
        assertEquals(expected, actual);
    }
}