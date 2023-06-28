package com.hw.cloudstorage.services.impl;

import com.hw.cloudstorage.model.entity.BlockedToken;
import com.hw.cloudstorage.repositories.BlacklistTokenRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TokenServiceImplTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private BlacklistTokenRepository blackList;

    private final static String token = "some.test.unreal.token";

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start TokenServiceImpl Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---TokenServiceImpl Class Test Completed---");
    }


    @Test
    @Order(1)
    void isBlocked() {

        when(blackList.findById(String.valueOf(anyLong()))).thenReturn(Optional.empty());
        assertFalse(tokenService.isBlocked(token));
    }

    @Test
    @Order(2)
    void save() {
        BlockedToken expected = mock(BlockedToken.class);
        when(blackList.save(expected)).thenReturn(expected);

        BlockedToken actual = tokenService.save(expected);

        assertEquals(expected, actual);
    }
}