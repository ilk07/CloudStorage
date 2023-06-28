package com.hw.cloudstorage.services.impl;

import com.hw.cloudstorage.model.entity.BlockedToken;
import com.hw.cloudstorage.repositories.BlacklistTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class LogoutHandlerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    BlacklistTokenRepository blacklistTokenRepository;

    private final static String TEST_TOKEN_HEADER = "auth-token";


    @Test
    void logout() throws Exception {

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJ1c2VybmFtZSI6InRlc3QtdXNlciIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjQwODU1NTk3ODksImV4cCI6NDA4NTU1OTc4OX0.W9NSwfjamPtSI5Bxc3yncGVsHIp0mi86Jo1NDc2_bso";
        String bearerToken = "Bearer " + token;

        mockMvc.perform(
                        post("/logout")
                                .header(TEST_TOKEN_HEADER, bearerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().is(200));


            String actual = "";
            Optional<BlockedToken> bt = blacklistTokenRepository.findById(token);
            if(bt.isPresent()){
                actual = bt.get().getToken();
                blacklistTokenRepository.delete(bt.get());
            }

            assertTrue(actual.contains(token));



    }


}



