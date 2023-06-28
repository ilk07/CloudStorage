package com.hw.cloudstorage.dto;

import com.hw.cloudstorage.model.enums.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FileEntityDtoTest {

    FileEntityDto sut;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start FileEntityDto Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---FileEntityDto Class Test Completed---");
    }

    @BeforeEach
    void setUp() {
        byte[] fileData = "test string".getBytes();
        Status status = Status.ACTIVE;

        sut = new FileEntityDto(
                "fileName",
                "uploadName",
                "fileExtension",
                "fileFolder",
                100L,
                "contentType",
                fileData,
                1L,
                status
        );
    }

    @Test
    void FileEntityDto() {
        assertInstanceOf(FileEntityDto.class, sut);
    }

    @Test
    void getFilename() {
        String expected = "fileName";
        String actual = sut.getFilename();

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
    void getFileData() {

        String expected = "test string";
        String actual = new String(sut.getFileData());

        assertEquals(expected, actual);
    }

    @Test
    void getUserId() {
        Long expected = 1L;
        Long actual = sut.getUserId();

        assertEquals(expected, actual);
    }

    @Test
    void getStatus() {

        Status expected = Status.ACTIVE;
        Status actual = sut.getStatus();

        assertEquals(expected, actual);

    }

    @Test
    void setFilename() {

        String expected = "updateFileName";
        sut.setFilename(expected);
        String actual = sut.getFilename();

        assertEquals(expected, actual);

    }

    @Test
    void setFileExtension() {

        String expected = "updateFileExtension";
        sut.setFileExtension(expected);
        String actual = sut.getFileExtension();

        assertEquals(expected, actual);

    }

    @Test
    void setFileFolder() {

        String expected = "updateFileFolder";
        sut.setFileFolder(expected);
        String actual = sut.getFileFolder();

        assertEquals(expected, actual);
    }

    @Test
    void setContentType() {
        String expected = "updateContentType";
        sut.setContentType(expected);
        String actual = sut.getContentType();

        assertEquals(expected, actual);
    }

    @Test
    void setFileData() {
        String expected = "new test string";

        byte[] fileData = expected.getBytes();
        sut.setFileData(fileData);
        String actual = new String(sut.getFileData());

        assertEquals(expected, actual);

    }

    @Test
    void setUserId() {
        Long expected = 2L;
        sut.setUserId(expected);
        Long actual = sut.getUserId();

        assertEquals(expected, actual);
    }

    @Test
    void setStatus() {
        Status expected = Status.DELETED;
        sut.setStatus(expected);
        Status actual = sut.getStatus();

        assertEquals(expected, actual);
    }

}