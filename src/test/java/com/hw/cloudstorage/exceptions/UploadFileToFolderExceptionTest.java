package com.hw.cloudstorage.exceptions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UploadFileToFolderExceptionTest {

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start UploadFileToFolderException Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---UploadFileToFolderException Class Test Completed---");
    }


    @Test
    @MethodSource({"writeFileException"})
    public void itShouldThrowUploadFileToFolderException() {
        Exception exception = assertThrows(
                UploadFileToFolderException.class,
                () -> writeFileException()
        );

        assertTrue(exception.getMessage().contains("Upload File To Folder Exception"));
    }

    void writeFileException(){
        throw new UploadFileToFolderException("Upload File To Folder Exception");
    }


}