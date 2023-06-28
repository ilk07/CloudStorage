package com.hw.cloudstorage;

import com.hw.cloudstorage.config.JwtTokenProperties;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.repositories.FileRepository;
import com.hw.cloudstorage.repositories.UserRepository;
import com.jayway.jsonpath.JsonPath;
import com.redis.testcontainers.RedisContainer;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationUploadToFolderTests {

    @Autowired
    JwtTokenProperties tokenProperties;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserRepository userRepository;

    private final static String LOGIN_ENDPOINT = "/login";
    private final static String FILE_ENDPOINT = "/file";

    private final static String DEMO_USER_LOGIN = "test@test.com";
    private final static String DEMO_USER_PASSWORD = "password";
    private final static int REDIS_PORT = 6379;
    private final static String REDIS_PASS = "redispass";
    private final static int DB_PORT = 5432;
    private final static String UPLOAD_FOLDER = "test-files";


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


        registry.add("files.upload-folder", () -> UPLOAD_FOLDER);
        registry.add("files.to-folder", () -> true);
        registry.add("files.to-database", () -> false);
        registry.add("files.remove-on-delete", () -> true);
    }

    @AfterAll
    static void deleteTestFileUploadFolder() throws IOException {
        FileUtils.deleteDirectory(new File(UPLOAD_FOLDER));
    }

    @Test
    void endPointFile_whenPost_shouldUploadFileToFolder_status200() throws Exception {

        fileRepository.deleteAll();
        String uploadName = "upload-test.pdf";

        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        String authToken = tokenProperties.getBearer() + " " + getTokenForDemoUser();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        uploadName,
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart(FILE_ENDPOINT)
                                .file(file)
                                .param("filename", uploadName)
                                .header(tokenProperties.getHeader(), authToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        FileEntity fileEntity = fileRepository.findByUserAndUploadNameAndStatus(user, uploadName, Status.ACTIVE).orElse(null);

        File uploadedFile = new File(fileEntity.getFileFolder());
        assertNull(fileEntity.getBytes());
        assertTrue(uploadedFile.exists());
        assertEquals(uploadName, fileEntity.getUploadName());
    }

    @Test
    void endPointFile_whenDelete_shouldDeleteFileFromFolderAndDB_status200() throws Exception {

        fileRepository.deleteAll();
        String uploadName = "upload-test.pdf";

        User user = userRepository.findByUsername(DEMO_USER_LOGIN).orElse(null);

        String authToken = tokenProperties.getBearer() + " " + getTokenForDemoUser();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        uploadName,
                        MediaType.APPLICATION_PDF_VALUE,
                        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart(FILE_ENDPOINT)
                                .file(file)
                                .param("filename", uploadName)
                                .header(tokenProperties.getHeader(), authToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());


        FileEntity fileEntity = fileRepository.findByUserAndUploadNameAndStatus(user, uploadName, Status.ACTIVE).orElse(null);
        assertNotNull(fileEntity);

        File uploadedFile = new File(fileEntity.getFileFolder());
        assertTrue(uploadedFile.exists());

        mockMvc.perform(
                        delete(FILE_ENDPOINT)
                                .param("filename", uploadName)
                                .header(tokenProperties.getHeader(), authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200));

        FileEntity deletedEntity = fileRepository.findById(fileEntity.getId()).orElse(null);
        assertNull(deletedEntity);
        assertFalse(uploadedFile.exists());
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
}
