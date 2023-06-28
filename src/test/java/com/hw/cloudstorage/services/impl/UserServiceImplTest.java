package com.hw.cloudstorage.services.impl;

import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Principal principal;

    private final static String USER_NAME = "username";
    private final static Long USER_ID = 1L;

    @BeforeAll
    public static void startServiceTest() {
        System.out.println("---Start UserServiceImpl Service Class Test---");
    }

    @AfterAll
    public static void endServiceTest() {
        System.out.println("---UserServiceImpl Service Class Test Completed---");
    }

    @Test
    void findByUsername_shouldReturnUser() {
        User expected = mock(User.class);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.ofNullable(expected));

        User actual = userService.findByUsername(USER_NAME);
        assertEquals(expected, actual);
    }

    @Test
    void findByUsername_shouldReturnNull() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        User actual = userService.findByUsername(USER_NAME);

        assertNull(actual);

    }

    @Test
    void findById_shouldReturnUser() {

        User expected = mock(User.class);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(expected));

        User actual = userService.findById(USER_ID);
        assertEquals(expected, actual);

    }

    @Test
    void findById_shouldReturnNull() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        User actual = userService.findById(USER_ID);

        assertNull(actual);
    }

    @Test
    void getUser_shouldReturnUser() {

        User expected = mock(User.class);
        when(principal.getName()).thenReturn(USER_NAME);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.ofNullable(expected));

        User actual = userService.getUser(principal);
        assertEquals(expected, actual);

    }

    @Test
    void getUser_shouldThrowUsernameNotFoundException() {

        when(principal.getName()).thenReturn(USER_NAME);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser(principal);
                });
    }
}