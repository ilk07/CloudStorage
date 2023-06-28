package com.hw.cloudstorage.advice;

import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.ErrorType;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.repositories.FileRepository;
import com.hw.cloudstorage.services.impl.FileServiceImpl;
import com.hw.cloudstorage.services.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ExceptionHandlerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    Principal principal;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private FileServiceImpl fileService;

    @MockBean
    private FileRepository storage;

    private final static String TEST_LOGIN = "test-user";
    private final static String TEST_PASSWORD = "test-password";
    private final static String TEST_TOKEN = "test.token";
    private final static String TEST_TOKEN_HEADER = "auth-token";
    private final static String TEST_ROLE = "USER";


    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start ExceptionHandlerAdvice Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---ExceptionHandlerAdvice Class Test Completed---");
    }

    @Test
    @WithMockUser(username = TEST_LOGIN, password = TEST_PASSWORD, roles = TEST_ROLE)
    void illegalArgumentException_shouldReturnResponse400_WithErrorTypeId_110() throws Exception {

        String errorId = String.valueOf(ErrorType.INVALID_ARGUMENTS_DATA.getErrorId());

        User user = mock(User.class);
        when(userService.getUser(any(Principal.class))).thenReturn(user);
        when(storage.countAllByUserAndUploadNameAndStatus(any(User.class), any(String.class), any(Status.class))).thenReturn(1L);
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
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("id")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(errorId)));
    }

    @Test
    void badCredentialsExceptionHandler_shouldReturnResponse400_WithErrorTypeId101() throws Exception {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("login", "invalid login");
        jsonObj.put("password", "invalid password");

        String requestBody = String.valueOf(jsonObj);

        mockMvc.perform(
                        post("/login")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("id")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("message")))
                .andExpect(result -> assertTrue((result.getResponse().getContentAsString().contains("103"))));
    }

    @Test
    void handleMethodArgumentNotValid_shouldReturnResponse400_WithErrorTypeId_110() throws Exception {

        String errorId = String.valueOf(ErrorType.INVALID_ARGUMENTS_DATA.getErrorId());

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("login", "");
        jsonObj.put("password", "");

        String requestBody = String.valueOf(jsonObj);

        mockMvc.perform(
                        post("/login")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("login")))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("password")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains((errorId))));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN, password = TEST_PASSWORD, roles = TEST_ROLE)
    void methodArgumentTypeMismatchException_shouldReturnResponse400_WithErrorTypeId_110_invalidTypeOfLimitParam() throws Exception {

        String errorId = String.valueOf(ErrorType.INVALID_ARGUMENTS_DATA.getErrorId());

        User user = mock(User.class);
        when(userService.getUser(any(Principal.class))).thenReturn(user);

        mockMvc.perform(
                        get("/list")
                                .param("limit", "text")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("limit")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(errorId)));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN, password = TEST_PASSWORD, roles = TEST_ROLE)
    void handleMissingServletRequestParameter_shouldReturnResponse400_WithErrorTypeId_110_missedLimitParam() throws Exception {

        String errorId = String.valueOf(ErrorType.INVALID_ARGUMENTS_DATA.getErrorId());

        User user = mock(User.class);
        when(userService.getUser(any(Principal.class))).thenReturn(user);

        mockMvc.perform(
                        get("/list")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingServletRequestParameterException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("limit")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(errorId)));
    }
}