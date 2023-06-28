package com.hw.cloudstorage.model.entity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class FileEntityTest extends BaseEntityTest {

    FileEntity sut;

    @Mock
    User user;
    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start FileEntity Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---FileEntity Class Test Completed---");
    }

    @BeforeEach
    void setUp() {
        sut = new FileEntity();
        sut.setName("name");
        sut.setUploadName("uploadName");
        sut.setFileExtension("fileExtension");
        sut.setFileFolder("fileFolder");
        sut.setSize(100L);
        sut.setContentType("contentType");
        sut.setBytes("test".getBytes());
        sut.setUser(user);
    }

    @Test
    void getName() {
        String expected = "name";
        String actual = sut.getName();
        assertEquals(expected, actual);
    }

    @Test
    void getUploadName() {
        String expected = "uploadName";
        String actual = sut.getUploadName();
        assertEquals(expected, actual);
    }

    @Test
    void getFileExtension() {
        String expected = "fileExtension";
        String actual = sut.getFileExtension();
        assertEquals(expected, actual);
    }

    @Test
    void getFileFolder() {
        String expected = "fileFolder";
        String actual = sut.getFileFolder();
        assertEquals(expected, actual);
    }

    @Test
    void getSize() {
        Long expected = 100L;
        Long actual = sut.getSize();
        assertEquals(expected, actual);
    }

    @Test
    void getContentType() {
        String expected = "contentType";
        String actual = sut.getContentType();
        assertEquals(expected, actual);
    }

    @Test
    void getUser() {
        User expected = user;
        User actual = sut.getUser();
        assertEquals(expected, actual);
    }

    @Test
    void setName() {
        String expected = "newName";
        sut.setName(expected);
        String actual = sut.getName();
        assertEquals(expected, actual);
    }

    @Test
    void setUploadName() {
        String expected = "newUploadName";
        sut.setUploadName(expected);
        String actual = sut.getUploadName();
        assertEquals(expected, actual);
    }

    @Test
    void setFileExtension() {
        String expected = "newFileExtension";
        sut.setFileExtension(expected);
        String actual = sut.getFileExtension();
        assertEquals(expected, actual);
    }

    @Test
    void setFileFolder() {
        String expected = "newFileFolder";
        sut.setFileFolder(expected);
        String actual = sut.getFileFolder();
        assertEquals(expected, actual);
    }

    @Test
    void setSize() {
        Long expected = 25L;
        sut.setSize(expected);
        Long actual = sut.getSize();
        assertEquals(expected, actual);
    }

    @Test
    void setContentType() {
        String expected = "newContentType";
        sut.setContentType(expected);
        String actual = sut.getContentType();
        assertEquals(expected, actual);
    }


    @Test
    void setUser() {
        User userMock = mock(User.class);
        sut.setUser(userMock);
        User expected = userMock;
        User actual = sut.getUser();
        assertEquals(expected, actual);
    }
}