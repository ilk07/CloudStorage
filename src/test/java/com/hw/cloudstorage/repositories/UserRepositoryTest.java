package com.hw.cloudstorage.repositories;

import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    UserRepository repository;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start UserRepository interface Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---UserRepository interface Test Completed---");
    }


    @Test
    @DisplayName("Find all Users")
    public void findAll_shouldFindAllUsers() {
        int expected = (int) repository.count();

        List<User> users = repository.findAll();
        int actual = users.size();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Store new user")
    public void save_shouldStoreNewUser() {

        User user = createUserForOneTest("username", Status.ACTIVE);

        User actual = repository.save(user);

        MatcherAssert.assertThat(actual, allOf(
                hasProperty("id", greaterThan(0L)),
                hasProperty("email", equalTo("email")),
                hasProperty("password", equalTo("password")),
                hasProperty("username", equalTo("username")),
                hasProperty("status", equalTo(Status.ACTIVE))
        ));
    }

    @Test
    void findById_shouldFindUserWithGivenId() {

        User user = createUserForOneTest("username", Status.ACTIVE);

        User expected = entityManager.persist(user);
        User actual = repository.findById(expected.getId()).get();

        assertEquals(expected, actual);

    }

    @Test
    void findByUsername_shouldFindUserByUsername() {
        String username = "test-user-find-by-username";
        User user = createUserForOneTest(username, Status.ACTIVE);
        User expected = entityManager.persist(user);

        User actual = repository.findByUsername(username).orElse(null);

        assertTrue(actual != null);
        assertEquals(expected, actual);
        assertTrue(actual.getUsername().equals(username));

    }


    @Test
    @DisplayName("Delete User by id")
    void deleteById_shouldDeleteUserFromRepository() {
        User user = createUserForOneTest("username", Status.ACTIVE);
        entityManager.persist(user);

        assertTrue(repository.findAll().contains(user));

        repository.deleteById(user.getId());

        assertFalse(repository.findAll().contains(user));
    }

    @Test
    @DisplayName("Delete all users")
    void deleteAll_shouldDeleteAllRoles() {
        User user = createUserForOneTest("username", Status.ACTIVE);
        entityManager.persist(user);

        assertTrue(repository.count() > 0);

        repository.deleteAll();
        List<User> users = repository.findAll();
        assertTrue(users.isEmpty());

    }

    private User createUserForOneTest(String username, Status status) {
        User user = new User();
        user.setEmail("email");
        user.setPassword("password");
        user.setStatus(status);
        user.setUsername(username);

        return user;
    }


}