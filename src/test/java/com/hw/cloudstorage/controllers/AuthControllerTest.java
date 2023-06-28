package com.hw.cloudstorage.controllers;

import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.security.jwt.JwtTokenProvider;
import com.hw.cloudstorage.services.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    AuthenticationManager authenticationManager;

    private final static String TEST_LOGIN = "test-user";
    private final static String TEST_PASSWORD = "test-password";
    private final static String TEST_TOKEN = "test.token";
    private final static String TEST_TOKEN_HEADER = "auth-token";
    private final static String TEST_ROLE = "USER";



    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start AuthController Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---AuthController Class Test Completed---");
    }

    @Test
    @WithMockUser(username = TEST_LOGIN, password = TEST_PASSWORD, roles = TEST_ROLE)
    void getAuthToken_shouldReturnAuthToken() throws Exception {

        User userMock = mock(User.class);
        when(userMock.getStatus()).thenReturn(Status.ACTIVE);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("login", TEST_LOGIN);
        jsonObj.put("password", TEST_PASSWORD);

        String requestBody = String.valueOf(jsonObj);

        Mockito.when(userService.findByUsername(any(String.class))).thenReturn(userMock);
        Mockito.when(jwtTokenProvider.createToken(userMock)).thenReturn(TEST_TOKEN);

        mockMvc.perform(
                        post("/login")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(TEST_TOKEN_HEADER)))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(TEST_TOKEN)));
    }

    @Test
    @WithMockUser(username = TEST_LOGIN, password = TEST_PASSWORD, roles = TEST_ROLE)
    void getAuthToken_shouldReturnStatusUnauthorized() throws Exception {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("login", TEST_LOGIN);
        jsonObj.put("password", TEST_PASSWORD);

        String requestBody = String.valueOf(jsonObj);

        Mockito.when(userService.findByUsername(any(String.class))).thenReturn(null);

        mockMvc.perform(
                        post("/login")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().is(401));
    }


    @Test
    void getAuthToken_shouldReturnArgumentsException() throws Exception {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("login", TEST_LOGIN);

        String requestBody = String.valueOf(jsonObj);

        mockMvc.perform(
                        post("/login")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("password is empty")));
    }

    @Test
    void logout_shouldReturnOk() throws Exception{
        mockMvc.perform(
                        post("/logout")
                                .header(TEST_TOKEN_HEADER, TEST_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200));
    }
}