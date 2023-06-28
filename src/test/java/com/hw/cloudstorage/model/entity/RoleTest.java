package com.hw.cloudstorage.model.entity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RoleTest extends BaseEntityTest {
    Role sut;
    @Mock
    User user;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start Role Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---Role Class Test Completed---");
    }

    @BeforeEach
    void setUp() {
        sut = new Role("ADMIN", Collections.singletonList(user));
    }

    @Test
    void getName() {
        String expected = "ADMIN";
        String actual = sut.getName();
        assertEquals(expected, actual);
    }

    @Test
    void getUsers() {
        int expected = 1;
        int actual = sut.getUsers().size();
        assertEquals(expected, actual);
    }

    @Test
    void setName() {
        String expected = "MANAGER";
        sut.setName(expected);
        String actual = sut.getName();
        assertEquals(expected, actual);
    }

    @Test
    void setUsers() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        List<User> expected = Arrays.asList(user1, user2);
        sut.setUsers(expected);
        List<User> actual = sut.getUsers();

        assertTrue(Arrays.equals(expected.toArray(), actual.toArray()));

    }

}