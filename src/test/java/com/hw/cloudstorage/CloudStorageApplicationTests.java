package com.hw.cloudstorage;

import com.hw.cloudstorage.config.JwtTokenProperties;
import com.hw.cloudstorage.model.entity.BlockedToken;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.repositories.BlacklistTokenRepository;
import com.hw.cloudstorage.repositories.FileRepository;
import com.hw.cloudstorage.repositories.UserRepository;
import com.jayway.jsonpath.JsonPath;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

    @Autowired
    JwtTokenProperties tokenProperties;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BlacklistTokenRepository blacklistTokenRepository;
    private final static String LOGIN_ENDPOINT = "/login";
    private final static String LOGOUT_ENDPOINT = "/logout";
    private final static String FILE_ENDPOINT = "/file";
    private final static String LIST_ENDPOINT = "/list";

    private final static String DEMO_USER_LOGIN = "test@test.com";
    private final static String DEMO_USER_PASSWORD = "password";

    private final static int REDIS_PORT = 6379;
    private final static String REDIS_PASS = "redispass";
    private final static int DB_PORT = 5432;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withExposedPorts(DB_PORT)
            .waitingFor(Wait.forLogMessage("database system is ready to accept connections", 1))
            .waitingFor(Wait.forListeningPort());

    @Container
    private static final RedisContainer redis =
            new RedisContainer(DockerImageName.parse("redis:6.0"))
                    .withEnv("requirepass", REDIS_PASS)
                    .withExposedPorts(REDIS_PORT)
                    .waitingFor(Wait.forLogMessage("* Ready to accept connections", 1))
                    .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(REDIS_PORT).toString());
        registry.add("files.to-folder", () -> false);
        registry.add("files.remove-on-delete", () -> false);
        registry.add("files.allow-types", () -> "txt, pdf");
    }

    @Test
    void endPointLogin_whenPostCredentials_shouldReturnErrorId101_BadCredentials_BadCredentialsException() throws Exception {

        String login = "invalid-login";
        String password = "invalid-password";

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("login", login);
        jsonObj.put("password", password);

        String requestBody = String.valueOf(jsonObj);

        mockMvc.perform(
                        post("/login")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("message")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("id")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("101")));
    }

    @Test
    void endPointLogin_whenPostCredentials_shouldReturnErrorId110_BadCredentials_MethodArgumentNotValidException() throws Exception {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("login", "some.login");

        String requestBody = String.valueOf(jsonObj);

        mockMvc.perform(
                        post("/login")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("message")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("id")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("110")));
    }

    @Test
    void endPointLogin_whenPostCredentials_shouldReturnAuthToken_status200() throws Exception {

        JSONObject requestData = new JSONObject();
        requestData.put("login", DEMO_USER_LOGIN);
        requestData.put("password", DEMO_USER_PASSWORD);

        mockMvc.perform(
                        post("/login")
                                .content(String.valueOf(requestData))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(tokenProperties.getHeader())));
    }

    @Test
    void endPointLogout_whenPost_shouldAddTokenToBlackList_logoutUser_status200() throws Exception {

        String token = getTokenForDemoUser();
        String authToken = tokenProperties.getBearer() + " " + token;

        mockMvc.perform(
                        post(LOGOUT_ENDPOINT)
                                .header(tokenProperties.getHeader(), authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200));

        BlockedToken blockedToken = blacklistTokenRepository.findById(token).orElse(null);
        assertNotNull(blockedToken);

    }

    @Test
    void endPointList_whenGet_shouldReturnUserFileList_status200() throws Exception {

        String fileUploadName = "fileUploadNameForTest";

        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        FileEntity fileEntity = createFileEntity(fileUploadName, user, Status.ACTIVE);

        fileRepository.save(fileEntity);

        String authToken = tokenProperties.getBearer() + " " + getTokenForDemoUser();

        mockMvc.perform(
                        get(LIST_ENDPOINT)
                                .param("limit", "3")
                                .header(tokenProperties.getHeader(), authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(fileUploadName)));
    }

    @Test
    void endPointFile_whenPut_shouldRenameFile_status200() throws Exception {

        String fileUploadName = "nameTest.txt";
        String expected = "updatedNameTest.txt";

        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        FileEntity fileEntity = createFileEntity(fileUploadName, user, Status.ACTIVE);

        fileRepository.save(fileEntity);

        JSONObject requestData = new JSONObject();
        requestData.put("filename", expected);

        String token = getTokenForDemoUser();
        String authToken = tokenProperties.getBearer() + " " + token;

        mockMvc.perform(
                        put(FILE_ENDPOINT)
                                .param("filename", fileUploadName)
                                .header(tokenProperties.getHeader(), authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestData.toString())
                )
                .andExpect(status().is(200));

        FileEntity updatedEntity = fileRepository.findById(fileEntity.getId()).orElse(null);
        String actual = updatedEntity.getUploadName();
        assertEquals(expected, actual);

    }

    @Test
    void endPointFile_whenGet_shouldReturnFileToDownload_status200() throws Exception {
        String fileToDownload = "fileDownloadNameForTest.txt";

        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        FileEntity fileEntity = createFileEntity(fileToDownload, user, Status.ACTIVE);

        fileRepository.save(fileEntity);

        String authToken = tokenProperties.getBearer() + " " + getTokenForDemoUser();

        mockMvc.perform(
                        get(FILE_ENDPOINT)
                                .param("filename", fileToDownload)
                                .header(tokenProperties.getHeader(), authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().is(200))
                .andExpect(result -> assertEquals(fileToDownload, result.getResponse().getContentAsString()));
    }

    @Test
    void endPointFile_whenPost_shouldUploadFile_status200() throws Exception {

        fileRepository.deleteAll();

        String expected = "upload-test.pdf";
        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        String authToken = tokenProperties.getBearer() + " " + getTokenForDemoUser();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        expected,
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart(FILE_ENDPOINT)
                                .file(file)
                                .param("filename", expected)
                                .header(tokenProperties.getHeader(), authToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        FileEntity fileEntity = fileRepository.findByUserAndUploadNameAndStatus(user, expected, Status.ACTIVE).orElse(null);
        String actual = fileEntity.getUploadName();

        assertEquals(expected, actual);

    }

    @Test
    void endPointFile_whenPost_shouldUploadFile_ignoreSameFileWithDeletedStatus_status200() throws Exception {

        fileRepository.deleteAll();

        String expected = "upload-test.pdf";
        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        FileEntity fileEntity = createFileEntity(expected, user, Status.DELETED);

        fileRepository.save(fileEntity);

        String authToken = tokenProperties.getBearer() + " " + getTokenForDemoUser();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        expected,
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart(FILE_ENDPOINT)
                                .file(file)
                                .param("filename", expected)
                                .header(tokenProperties.getHeader(), authToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        FileEntity uploadedFileEntity = fileRepository.findByUserAndUploadNameAndStatus(user, expected, Status.ACTIVE).orElse(null);

        String actual = fileEntity.getUploadName();

        assertEquals(expected, actual);
        assertNotEquals(fileEntity.getId(), uploadedFileEntity.getId());

    }

    @Test
    void endPointFile_whenPost_shouldReturnErrorId110_BadRequest_status400() throws Exception {

        fileRepository.deleteAll();

        String uploadFileName = "upload-test.pdf";
        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        FileEntity fileEntity = createFileEntity(uploadFileName, user, Status.ACTIVE);

        fileRepository.save(fileEntity);

        String authToken = tokenProperties.getBearer() + " " + getTokenForDemoUser();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "upload-test.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart(FILE_ENDPOINT)
                                .file(file)
                                .param("filename", uploadFileName)
                                .header(tokenProperties.getHeader(), authToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("id")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("110")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("File with same name has already been uploaded")));
    }

    @Test
    void endPointFile_whenPost_shouldReturnErrorId112_FileTypeDisallow_BadRequest_status400() throws Exception {

        String uploadFileName = "upload-test.docx";

        String authToken = tokenProperties.getBearer() + " " + getTokenForDemoUser();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        uploadFileName,
                        MediaType.MULTIPART_FORM_DATA_VALUE,
                        "docx".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart(FILE_ENDPOINT)
                                .file(file)
                                .param("filename", uploadFileName)
                                .header(tokenProperties.getHeader(), authToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("id")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("110")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("File type is not allowed")));
    }


    @Test
    void endPointFile_whenDelete_shouldChangeStatusToDeleted_status200() throws Exception {

        fileRepository.deleteAll();

        String fileToDelete = "deleteTest.txt";

        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        FileEntity fileEntity = createFileEntity(fileToDelete, user, Status.ACTIVE);

        fileRepository.save(fileEntity);

        Long fileId = fileEntity.getId();

        String token = getTokenForDemoUser();

        String authToken = tokenProperties.getBearer() + " " + token;

        mockMvc.perform(
                        delete(FILE_ENDPOINT)
                                .param("filename", fileToDelete)
                                .header(tokenProperties.getHeader(), authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200));

        FileEntity deletedEntity = fileRepository.findById(fileId).orElse(null);
        Status expected = Status.DELETED;
        Status actual = deletedEntity.getStatus();

        assertEquals(expected, actual);

    }

    private String getTokenForDemoUser() throws Exception {

        JSONObject requestData = new JSONObject();
        requestData.put("login", DEMO_USER_LOGIN);
        requestData.put("password", DEMO_USER_PASSWORD);

        MvcResult result = mockMvc.perform(
                        post(LOGIN_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestData.toString())
                )
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$." + tokenProperties.getHeader());
        return token;
    }

    private FileEntity createFileEntity(String uploadName, User user, Status status) {

        byte[] fileData = uploadName.getBytes();

        FileEntity fileEntity = FileEntity.builder()
                .name("filename.txt")
                .uploadName(uploadName)
                .contentType("text/plain")
                .size(10L)
                .fileFolder("fileFolder")
                .fileExtension("fileExtension")
                .bytes(fileData)
                .user(user)
                .build();
        fileEntity.setStatus(status);

        return fileEntity;
    }
}
