package com.hw.cloudstorage.repositories;

import com.hw.cloudstorage.model.FileEntityNameSize;
import com.hw.cloudstorage.model.entity.FileEntity;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class FileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository repository;

    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start FileRepository Class Test---");

    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---FileRepository Class Test Completed---");
    }



    @Test
    @DisplayName("Find all files in empty repository")
    public void should_find_no_files_if_repository_is_empty() {
        List<FileEntity> files = repository.findAll();

        assertThat(files.isEmpty());
    }

    @Test
    @DisplayName("Store file")
    public void saveFileEntity_shouldStoreFileEntity() {

        User user = userRepository.findById(1L).orElse(null);

        FileEntity fileEntity = createFileEntityForOneTest(user, Status.ACTIVE);

        FileEntity actual = repository.save(fileEntity);

        MatcherAssert.assertThat(actual, allOf(
                hasProperty("id", greaterThan(0L)),
                hasProperty("name", equalTo("filename")),
                hasProperty("uploadName", equalTo("uploadName")),
                hasProperty("contentType", equalTo("contentType")),
                hasProperty("size", equalTo(100L)),
                hasProperty("fileFolder", equalTo("fileFolder")),
                hasProperty("fileExtension", equalTo("fileExtension")),
                hasProperty("status", equalTo(Status.ACTIVE))
        ));

    }

    @Test
    @DisplayName("Find all files in not empty repository")
    public void findAll_shouldReturnAllEntity() {

        User user1 = userRepository.findById(1L).orElse(null);
        User user2 = userRepository.findById(2L).orElse(null);

        FileEntity fileEntity1 = createFileEntityForOneTest(user1, Status.ACTIVE);
        entityManager.persist(fileEntity1);

        FileEntity fileEntity2 = createFileEntityForOneTest(user1, Status.DELETED);
        entityManager.persist(fileEntity2);

        FileEntity fileEntity3 = createFileEntityForOneTest(user2, Status.ACTIVE);
        entityManager.persist(fileEntity3);

        List<FileEntity> actual = repository.findAll();

        int expected = 3;

        assertEquals(expected, actual.size());
        assertTrue(actual.contains(fileEntity1));
        assertTrue(actual.contains(fileEntity2));
        assertTrue(actual.contains(fileEntity3));
    }

    @Test
    @DisplayName("Find file by ID")
    public void findById_shouldFindFileEntityById() {
        User user = userRepository.findById(1L).orElse(null);

        FileEntity fileEntity = createFileEntityForOneTest(user, Status.ACTIVE);

        FileEntity expected = entityManager.persist(fileEntity);
        FileEntity actual = repository.findById(expected.getId()).get();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update file data")
    public void update_shouldFindByIdAndUpdateFileEntityStatus() {
        User user = userRepository.findById(1L).orElse(null);

        FileEntity fileEntity1 = createFileEntityForOneTest(user, Status.ACTIVE);
        entityManager.persist(fileEntity1);

        FileEntity entity = repository.findById(fileEntity1.getId()).get();

        entity.setStatus(Status.DELETED);

        repository.save(entity);

        FileEntity actual = repository.findById(fileEntity1.getId()).get();

        assertTrue(actual.getStatus().equals(Status.DELETED));

    }

    @Test
    @DisplayName("Find file by user, filename and status")
    public void findByUserAndUploadNameAndStatus_shouldFindRelativeEntity() {

        User user = userRepository.findById(1L).orElse(null);

        FileEntity fileEntity1 = createFileEntityForOneTest(user, Status.ACTIVE);
        entityManager.persist(fileEntity1);

        FileEntity fileEntity2 = createFileEntityForOneTest(user, Status.DELETED);
        entityManager.persist(fileEntity2);

        FileEntity activeFile = repository.findByUserAndUploadNameAndStatus(
                user,
                "uploadName",
                Status.ACTIVE
        ).orElse(null);
        FileEntity deletedFile = repository.findByUserAndUploadNameAndStatus(
                user,"uploadName", Status.DELETED
        ).orElse(null);

        assertEquals(fileEntity1, activeFile);
        assertEquals(fileEntity2, deletedFile);
    }

    @Test
    @DisplayName("Count files by user, filename and status")
    void countAllByUserAndUploadNameAndStatus_shouldCountUserFilesWithGivenStatus() {
        User user = userRepository.findById(1L).orElse(null);

        FileEntity fileEntity1 = createFileEntityForOneTest(user, Status.ACTIVE);
        entityManager.persist(fileEntity1);

        FileEntity fileEntity2 = createFileEntityForOneTest(user, Status.DELETED);
        entityManager.persist(fileEntity2);

        long expected = 1;
        long actual = repository.countAllByUserAndUploadNameAndStatus(user, "uploadName", Status.ACTIVE);

        assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Delete file by id")
    void deleteById() {
        User user1 = userRepository.findById(1L).orElse(null);
        User user2 = userRepository.findById(2L).orElse(null);

        FileEntity fileEntity1 = createFileEntityForOneTest(user1, Status.ACTIVE);
        entityManager.persist(fileEntity1);

        FileEntity fileEntity2 = createFileEntityForOneTest(user1, Status.DELETED);
        entityManager.persist(fileEntity2);

        FileEntity fileEntity3 = createFileEntityForOneTest(user2, Status.ACTIVE);
        entityManager.persist(fileEntity3);

        repository.deleteById(fileEntity1.getId());

        List<FileEntity> files = repository.findAll();
        int expected = 2;
        int actual = files.size();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete all files")
    public void deleteAll_shouldDeleteAllFileEntity() {
        User user1 = userRepository.findById(1L).orElse(null);
        User user2 = userRepository.findById(2L).orElse(null);

        FileEntity fileEntity1 = createFileEntityForOneTest(user1, Status.ACTIVE);
        entityManager.persist(fileEntity1);

        FileEntity fileEntity2 = createFileEntityForOneTest(user1, Status.DELETED);
        entityManager.persist(fileEntity2);

        FileEntity fileEntity3 = createFileEntityForOneTest(user2, Status.ACTIVE);
        entityManager.persist(fileEntity3);

        repository.deleteAll();
        List<FileEntity> files = repository.findAll();

        assertTrue(files.isEmpty());
    }

    @Test
    @DisplayName("Find all user files by id and status and return reduced data object")
    void findUserFileNameSizeByIdAndStatus_Named_shouldReturnFileEntityNameSizeObjectsList() {
        User user = userRepository.findById(1L).orElse(null);

        FileEntity fileEntity1 = createFileEntityForOneTest(user, Status.ACTIVE);
        entityManager.persist(fileEntity1);

        FileEntity fileEntity2 = createFileEntityForOneTest(user, Status.DELETED);
        entityManager.persist(fileEntity2);

        List<FileEntityNameSize> files = repository.findUserFileNameSizeByIdAndStatus_Named(1l, String.valueOf(Status.ACTIVE), 5);

        int expected = 1;
        int actual = files.size();

        assertEquals(expected, actual);
    }


    private FileEntity createFileEntityForOneTest(User user, Status status){
        FileEntity fileEntity = FileEntity.builder()
                .name("filename")
                .uploadName("uploadName")
                .contentType("contentType")
                .size(100L)
                .fileFolder("fileFolder")
                .fileExtension("fileExtension")
                .user(user)
                .build();
        fileEntity.setStatus(status);

        return fileEntity;
    }
}