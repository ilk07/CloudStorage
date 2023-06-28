package com.hw.cloudstorage.security.jwt;

import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.services.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JwtUserDetailsServiceTest {

    private JwtUserDetailsService sut;
    @Mock
    UserService userService;

    private final static String USERNAME = "username";

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start JwtUserDetailsService Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---JwtUserDetailsService Class Test Completed---");
    }

    @BeforeEach
    void initOneTest() {
        sut = new JwtUserDetailsService(userService);
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionUserNotFound() {

        when(userService.findByUsername(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> {
                    sut.loadUserByUsername(USERNAME);
                });
    }

    @Test
    void loadUserByUsername_shouldReturnUser() {

        String expected = USERNAME;

        User user = mock(User.class);
        when(user.getUsername()).thenReturn(expected);
        when(user.getStatus()).thenReturn(Status.ACTIVE);

        when(userService.findByUsername(anyString())).thenReturn(user);

        UserDetails userDetails = sut.loadUserByUsername(expected);
        String actual = userDetails.getUsername();
        assertEquals(expected, actual);

    }

}