package com.hw.cloudstorage.security.jwt;

import com.hw.cloudstorage.model.entity.Role;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JwtUserFactoryTest {

    @Mock
    Role role;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start JwtUserFactory Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---JwtUserFactory Class Test Completed---");
    }


    @Test
    void create() {
        List<Role> roles = new ArrayList<>();
        when(role.getName()).thenReturn("ROLE_TESTER");
        roles.add(role);
        User user = new User("userName", "password", "mail@mail", roles);
        user.setId(1L);
        user.setStatus(Status.ACTIVE);

        JwtUser actual = JwtUserFactory.create(user);
        assertThat(actual, allOf(
                hasProperty("id", equalTo(1L)),
                hasProperty("username", equalTo("userName")),
                hasProperty("password", equalTo("password"))
        ));

    }
}