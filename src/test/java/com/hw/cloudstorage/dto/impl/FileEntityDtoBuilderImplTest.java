package com.hw.cloudstorage.dto.impl;

import com.hw.cloudstorage.dto.FileEntityDto;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.services.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileEntityDtoBuilderImplTest {

    @InjectMocks
    private FileEntityDtoBuilderImpl sut = new FileEntityDtoBuilderImpl();

    @Mock
    UserServiceImpl userService;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start FileEntityDtoBuilderImpl Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---FileEntityDtoBuilderImpl Class Test Completed---");
    }


    @Test
    @DisplayName("FileEntityDtoBuilderImpl under test is Instance of FileEntityDtoBuilderImpl")
    public void sutIsInstanceOf() {
        assertInstanceOf(FileEntityDtoBuilderImpl.class, sut);
    }

    @Test
    void fromDtoToFileEntity() {
        User userMock = mock(User.class);
        when(userService.findById(anyLong())).thenReturn(userMock);
        byte[] bytes = "test".getBytes();
        FileEntityDto fileEntityDto = new FileEntityDto(
                "fileName",
                "uploadName",
                "fileExtension",
                "fileFolder",
                50L,
                "contentType",
                bytes,
                1L,
                Status.ACTIVE
        );
        FileEntity actual = sut.fromDtoToFileEntity(fileEntityDto);

        assertThat(actual, allOf(
                hasProperty("name", equalTo("fileName")),
                hasProperty("uploadName", equalTo("uploadName")),
                hasProperty("fileExtension", equalTo("fileExtension")),
                hasProperty("fileFolder", equalTo("fileFolder")),
                hasProperty("size", equalTo(50L)),
                hasProperty("contentType", equalTo("contentType")),
                hasProperty("user", equalTo(userMock)),
                hasProperty("status", equalTo(Status.ACTIVE)),
                hasProperty("bytes", equalTo(bytes))
        ));
    }
}

