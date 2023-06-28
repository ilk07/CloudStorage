package com.hw.cloudstorage.config;

import org.junit.jupiter.api.*;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


class AppPropertiesTest {

    AppProperties sut;
    private final static String host = "http://localhost";

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start AppProperties Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---AppProperties Class Test Completed---");
    }


    @BeforeEach
    void setUp() {
        sut = new AppProperties();
        sut.setName("App Test name");
        sut.setVersion("Test version");
        sut.setHost(host);
    }

    @Test
    @DisplayName("AppProperties in test is Instance of AppProperties")
    void AppProperties() {
        assertInstanceOf(AppProperties.class, sut);
    }

    @Test
    @DisplayName("AppProperties get name is App Test name")
    void getName() {
        String expected = "App Test name";
        String actual = sut.getName();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("AppProperties get version is Test version")
    void getVersion() {
        String expected = "Test version";
        String actual = sut.getVersion();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("AppProperties get host")
    void getHost() {
        String expected = host;
        sut.setName(expected);
        String actual = sut.getHost();

        assertEquals(expected, actual);
    }



    @Test
    @DisplayName("AppProperties setter for name can update name value")
    void setName() {
        String expected = "set-test-name";
        sut.setName(expected);
        String actual = sut.getName();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("AppProperties setter for version can update value")
    void setVersion() {
        String expected = "set-test-version";
        sut.setVersion(expected);
        String actual = sut.getVersion();

        assertEquals(expected, actual);
    }



    @Test
    @DisplayName("AppProperties setter for host can update host value")
    void setHost() {
        String expected = "set-new-host";
        sut.setHost(expected);
        String actual = sut.getHost();

        assertEquals(expected, actual);
    }

}