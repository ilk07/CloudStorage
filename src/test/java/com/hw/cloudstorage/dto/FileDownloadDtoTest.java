package com.hw.cloudstorage.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDownloadDtoTest {

    FileDownloadDto sut;

    @Mock
    InputStreamResource inputStreamResource;

    @Mock
    InputStreamResource anoherInputStreamResource;

    @BeforeEach
    void setUp() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("test", "test-name");
        sut = new FileDownloadDto();
        sut.setHeaders(httpHeaders);
        sut.setInputStreamResource(inputStreamResource);
    }

    @Test
    void getHeaders() {
        HttpHeaders actual = sut.getHeaders();
        assertTrue(actual.containsKey("test"));
    }

    @Test
    void getInputStreamResource() {

        final var expected = inputStreamResource;
        final var actual = sut.getInputStreamResource();

        assertEquals(expected, actual);

    }

    @Test
    void setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("test-update", "updated");
        sut.setHeaders(headers);
        final var actual = sut.getHeaders();

        assertTrue(actual.containsKey("test-update"));
    }

    @Test
    void setInputStreamResource() {
        final var expected = anoherInputStreamResource;
        sut.setInputStreamResource(expected);
        final var actual = sut.getInputStreamResource();

        assertEquals(expected, actual);
    }
}