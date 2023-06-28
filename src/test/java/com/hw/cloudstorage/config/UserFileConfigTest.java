package com.hw.cloudstorage.config;

import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserFileConfigTest {

    UserFileConfig sut;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start UserFileConfig Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---UserFileConfig Class Test Completed---");
    }


    @BeforeEach
    void setUp() {

        Set<String> allows = new HashSet<>();
        allows.add(".jpeg");
        allows.add(".txt");

        sut = new UserFileConfig();
        sut.setToDatabase(true);
        sut.setToFolder(true);
        sut.setUploadFolder("test-folder");
        sut.setRemoveOnDelete(true);
        sut.setAllowTypes(allows);
    }


    @Test
    @DisplayName("UserFileConfig in test is Instance of UserFileConfig")
    void UserFileConfig() {
        assertInstanceOf(UserFileConfig.class, sut);
    }

    @Test
    void getAllowTypes() {
        final var actual = sut.getAllowTypes();

        assertTrue(actual.contains(".txt"));
        assertTrue(actual.contains(".jpeg"));

    }

    @Test
    void getUploadFolder() {

        String actual = sut.getUploadFolder();
        String expected = "test-folder";

        assertEquals(expected, actual);

    }

    @Test
    void isToFolder() {
        boolean actual = sut.isToFolder();
        boolean expected = true;

        assertEquals(expected, actual);
    }

    @Test
    void isToDatabase() {
        boolean actual = sut.isToDatabase();
        boolean expected = true;

        assertEquals(expected, actual);
    }


    @Test
    void isRemoveOnDelete() {
        boolean actual = sut.isRemoveOnDelete();
        boolean expected = true;

        assertEquals(expected, actual);
    }

    @Test
    void setAllowTypes() {
        Set<String> newAllowTypesSet = new HashSet<>();
        newAllowTypesSet.add(".mpeg");

        sut.setAllowTypes(newAllowTypesSet);

        final var actual = sut.getAllowTypes();

        assertTrue(actual.contains(".mpeg"));
        assertFalse(actual.contains(".txt"));

    }

    @Test
    void setUploadFolder() {
        String expected = "new-test-upload-folder-name";
        sut.setUploadFolder(expected);
        String actual = sut.getUploadFolder();

        assertEquals(expected, actual);
    }

    @Test
    void setToFolder() {

        boolean expected = false;
        sut.setToFolder(expected);

        boolean actual = sut.isToFolder();

        assertEquals(expected, actual);
    }

    @Test
    void setToDatabase() {
        boolean expected = false;
        sut.setToDatabase(expected);

        boolean actual = sut.isToDatabase();

        assertEquals(expected, actual);
    }

    @Test
    void setRemoveOnDelete() {
        boolean expected = false;
        sut.setRemoveOnDelete(expected);

        boolean actual = sut.isRemoveOnDelete();

        assertEquals(expected, actual);
    }
}