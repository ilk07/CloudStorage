package com.hw.cloudstorage.repositories;

import com.hw.cloudstorage.model.entity.Role;
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
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    RoleRepository repository;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start RoleRepository interface Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---RoleRepository interface Test Completed---");
    }

    @Test
    @DisplayName("Store new role")
    public void saveRole_shouldStoreNewRole() {

        Role role = new Role();
        role.setStatus(Status.ACTIVE);
        role.setName("ROLE_TEST");

        Role actual = repository.save(role);

        MatcherAssert.assertThat(actual, allOf(
                hasProperty("id", greaterThan(0L)),
                hasProperty("name", equalTo("ROLE_TEST")),
                hasProperty("status", equalTo(Status.ACTIVE))
        ));
    }

    @Test
    @DisplayName("Find role by id")
    public void findById_shouldFindRoleWithGivenId() {
        Role role = new Role();
        role.setStatus(Status.ACTIVE);
        role.setName("ROLE_TEST");


        long expected = repository.save(role).getId();

        long actual = repository.findById(expected).orElse(null).getId();

        assertEquals(expected, actual);
    }


    @Test
    @DisplayName("Add new role and then find all roles include new one")
    public void findAll_shouldReturnAllRoleEntity() {

        int expected = (int) repository.count() + 1;

        Role role = new Role();
        role.setStatus(Status.ACTIVE);
        role.setName("ROLE_TEST");

        entityManager.persist(role);

        List<Role> actual = repository.findAll();

        assertEquals(expected, actual.size());
        assertTrue(actual.contains(role));
    }

    @Test
    @DisplayName("Update role data")
    public void update_shouldFindByIdAndUpdateFileEntityStatus() {

        Role role = new Role();
        role.setStatus(Status.ACTIVE);
        role.setName("ROLE_TEST_UPDATE");
        entityManager.persist(role);

        Role entity = repository.findById(role.getId()).get();
        entity.setStatus(Status.DELETED);
        repository.save(entity);

        Role actual = repository.findById(role.getId()).get();

        assertTrue(actual.getStatus().equals(Status.DELETED));

    }

    @Test
    @DisplayName("Delete role by id")
    void deleteById() {

        int expected = (int) repository.count();

        Role role = new Role();
        role.setStatus(Status.ACTIVE);
        role.setName("ROLE_TO_BE_DELETED");
        entityManager.persist(role);

        List<Role> roleListAfterInsert = repository.findAll();
        assertTrue(roleListAfterInsert.contains(role));

        repository.deleteById(role.getId());
        List<Role> roleListAfterDelete = repository.findAll();

        int actual = roleListAfterDelete.size();

        assertEquals(expected, actual);
        assertFalse(roleListAfterDelete.contains(role));

    }

    @Test
    @DisplayName("Delete all roles")
    void deleteAll_shouldDeleteAllRoles() {

        Role role = new Role();
        role.setStatus(Status.ACTIVE);
        role.setName("ROLE_TEST");
        entityManager.persist(role);

        assertTrue(repository.count() > 0);

        repository.deleteAll();
        List<Role> roles = repository.findAll();

        assertTrue(roles.isEmpty());

    }


}