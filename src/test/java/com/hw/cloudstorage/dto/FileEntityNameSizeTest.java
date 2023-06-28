package com.hw.cloudstorage.dto;

import com.hw.cloudstorage.model.FileEntityNameSize;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FileEntityNameSizeTest {
    FileEntityNameSize sut;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start FileEntityNameSizeDto Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---FileEntityNameSizeDto Class Test Completed---");
    }


    @BeforeEach
    void setUp() {
        sut = new FileEntityNameSize();
        sut.setSize(100L);
        sut.setFilename("fileName");
    }

    @Test
    @DisplayName("FileEntityNameSize in test is Instance of FileEntityNameSize")
    void FileEntityNameSize() {
        assertInstanceOf(FileEntityNameSize.class, sut);
    }

    @Test
    void getFilename() {
        String expected = "fileName";
        String actual = sut.getFilename();

        assertEquals(expected, actual);
    }

    @Test
    void getSize() {
        Long expected = 100L;
        Long actual = sut.getSize();

        assertEquals(expected, actual);
    }

    @Test
    void setFilename() {
        String expected = "newFileName";
        sut.setFilename(expected);
        String actual = sut.getFilename();

        assertEquals(expected, actual);
    }

    @Test
    void setSize() {
        Long expected = 25L;
        sut.setSize(expected);
        Long actual = sut.getSize();

        assertEquals(expected, actual);

    }

}