package com.hw.cloudstorage.controllers;

import com.hw.cloudstorage.dto.FileEntityDto;
import com.hw.cloudstorage.dto.impl.FileEntityDtoBuilderImpl;
import com.hw.cloudstorage.model.FileEntityNameSize;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.services.impl.FileServiceImpl;
import com.hw.cloudstorage.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc(addFilters = false)
class UserFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private FileServiceImpl fileService;

    @MockBean
    FileEntityDtoBuilderImpl fileEntityDtoBuilder;

    @MockBean
    Principal principal;

    private final static String TEST_USERNAME = "test-user";
    private final static String TEST_PASSWORD = "test-password";
    private final static String TEST_ROLE = "USER";
    private final static String TEST_TOKEN = "test.token";
    private final static String TEST_TOKEN_HEADER = "auth-token";

    @Test
    @WithMockUser(username = TEST_USERNAME, password = TEST_PASSWORD, roles = TEST_ROLE)
    void getUserFilesList_shouldReturnFileList() throws Exception {
        List<FileEntityNameSize> list = Arrays.asList(new FileEntityNameSize("test", 10L));
        Mockito.when(fileService.allFilesNameAndSizeByUserAndStatusWithLimit(userService.getUser(principal), Status.ACTIVE, 1))
                .thenReturn(list);

        mockMvc.perform(
                        get("/list")
                                .param("limit", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)

                )
                .andExpect(status().is(200))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("filename")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("test")));
    }

    @Test
    void getUserFile() throws Exception {
        String text = "test file data";
        String downloadFileName = "download-file-name.txt";
        User userMock = mock(User.class);
        FileEntity fileEntityMock = mock(FileEntity.class);
        when(userService.getUser(principal)).thenReturn(userMock);
        when(fileService.getByUserIdAndFileNameWithActiveStatus(any(User.class), any(String.class))).thenReturn(fileEntityMock);

        when(fileEntityMock.getBytes()).thenReturn(text.getBytes());

        mockMvc.perform(
                        get("/file")
                                .principal(principal)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .queryParam("filename", downloadFileName)
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN))

                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(text)))
                .andReturn();
    }

    @Test
    void uploadFile_shouldReturnErrorForNotAllowedFileType() throws Exception {

        when(fileService.isDuplicatedFileName(any(User.class), any(String.class))).thenReturn(false);
        when(fileService.isAllowableFileType(any(String.class))).thenReturn(false);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test_file.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/file")
                                .file(file)
                                .param("filename", "test_file.pdf")
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .principal(principal)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("message")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("id")));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, password = TEST_PASSWORD, roles = TEST_ROLE)
    void uploadFile_shouldReturnErrorForDuplicatedFileName() throws Exception {


        User user = mock(User.class);
        when(userService.getUser(any(Principal.class))).thenReturn(user);
        when(fileService.isDuplicatedFileName(any(User.class), any(String.class))).thenReturn(true);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test_file.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/file")
                                .file(file)
                                .param("filename", "test_file.pdf")
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .principal(principal)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("message")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("id")));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, password = TEST_PASSWORD, roles = TEST_ROLE)
    void uploadFile_shouldСallServiceUploadFileMethod() throws Exception {
        String filename = "upload-test.txt";
        User userMock = mock(User.class);

        FileEntity fileEntityMock = mock(FileEntity.class);
        when(userService.getUser(principal)).thenReturn(userMock);
        when(fileService.isDuplicatedFileName(userMock, filename)).thenReturn(false);
        when(fileService.isAllowableFileType(any(String.class))).thenReturn(true);
        when(fileService.generateUniqueFileName()).thenReturn("generated-file-name");
        when(fileEntityDtoBuilder.fromDtoToFileEntity(any(FileEntityDto.class))).thenReturn(fileEntityMock);
        doNothing().when(fileService).uploadFile(any(FileEntity.class), any(User.class));

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test_file.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/file")
                                .file(file)
                                .param("filename", filename)
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .principal(principal)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(fileService, times(1)).uploadFile(fileEntityMock, userMock);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, password = TEST_PASSWORD, roles = TEST_ROLE)
    void updateFile_shouldCallUpdateFileNameMethodInFileService() throws Exception {

        String oldFilename = "old-test-filename";
        String newFilename = "new-test-filename";

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("filename", newFilename);
        String requestBody = String.valueOf(jsonObj);

        mockMvc.perform(
                        put("/file")
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)
                                .param("filename", oldFilename)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().is(200));

        verify(fileService, times(1)).updateFileUploadName(userService.getUser(principal), oldFilename, newFilename);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, password = TEST_PASSWORD, roles = TEST_ROLE)
    void deleteFile() throws Exception {
        String filename = "test-filename";

        mockMvc.perform(
                        delete("/file")
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)
                                .param("filename", filename)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().is(200));

        verify(fileService, times(1)).deleteFileByUserAndFilename(userService.getUser(principal), filename);
    }

}