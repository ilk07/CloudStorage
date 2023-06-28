package com.hw.cloudstorage.services.impl;

import com.hw.cloudstorage.config.UserFileConfig;
import com.hw.cloudstorage.model.FileEntityNameSize;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.repositories.FileRepository;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileServiceImplTest {

    @Mock
    private UserFileConfig config;

    @InjectMocks
    FileServiceImpl fileService;

    @Mock
    private FileRepository storage;


    private final static byte[] TEST_FILE_DATA = "Test user file creating in folder".getBytes();
    private final static String TEST_FILENAME = "test-name-user-0.txt";
    private final static String TEST_UPLOAD_FOLDER = "test-upload-folder";
    private final Long TEST_USER_ID = 0L;

    private final static String SAVE_TO_FOLDER_NEW_FILE_NAME = "test-name-user-0-new-name.txt";



    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start FileServiceImpl Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---FileServiceImpl Class Test Completed---");
    }

    @Test
    @Order(1)
    void uploadFile() {
        User userMock = mock(User.class);
        FileEntity expected = mock(FileEntity.class);

        when(config.isToFolder()).thenReturn(false);
        when(storage.save(expected)).thenReturn(expected);

        fileService.uploadFile(expected, userMock);

        verify(storage, times(1)).save(expected);

    }

    @Test
    @Order(2)
    void allFilesNameAndSizeByUserAndStatusWithLimit() {
        List<FileEntityNameSize> expected = new ArrayList<>();
        FileEntityNameSize fileEntityNameSizeMock = mock(FileEntityNameSize.class);
        expected.add(fileEntityNameSizeMock);

        int limit = 5;
        User userMock = mock(User.class);
        Mockito.when(userMock.getId()).thenReturn(TEST_USER_ID);
        Mockito.when(storage.findUserFileNameSizeByIdAndStatus_Named(userMock.getId(), String.valueOf(Status.ACTIVE), limit)).thenReturn(expected);

        List<FileEntityNameSize> actual = fileService.allFilesNameAndSizeByUserAndStatusWithLimit(userMock, Status.ACTIVE, limit);

        assertEquals(expected, actual);

    }

    @Test
    @Order(3)
    void deleteFileByUserAndFilename_shouldCallStorageDeleteMethod() {
        User userMock = mock(User.class);
        FileEntity fileEntity = mock(FileEntity.class);

        Mockito.when(storage.findByUserAndUploadNameAndStatus(userMock, TEST_FILENAME, Status.ACTIVE)).thenReturn(Optional.ofNullable(fileEntity));
        Mockito.when(config.isRemoveOnDelete()).thenReturn(true);

        doAnswer(invocation -> {
            Object actual = invocation.getArgument(0);
            assertEquals(fileEntity, actual);
            return null;
        }).when(storage).delete(fileEntity);

        fileService.deleteFileByUserAndFilename(userMock, TEST_FILENAME);

    }

    @Test
    @Order(4)
    void deleteFileByUserAndFilename_shouldSetStatusDeletedToFileEntity() {
        User userMock = mock(User.class);
        FileEntity fileEntity = mock(FileEntity.class);

        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            assertEquals(Status.DELETED, arg0);
            return null;
        }).when(fileEntity).setStatus(Status.DELETED);


        Mockito.when(storage.findByUserAndUploadNameAndStatus(userMock, TEST_FILENAME, Status.ACTIVE)).thenReturn(Optional.ofNullable(fileEntity));
        Mockito.when(config.isRemoveOnDelete()).thenReturn(false);
        Mockito.when(storage.save(fileEntity)).thenReturn(fileEntity);

        fileService.deleteFileByUserAndFilename(userMock, TEST_FILENAME);
    }

    @Test
    @Order(5)
    void updateFileUploadName() {
        User userMock = mock(User.class);
        FileEntity fileEntityMock = mock(FileEntity.class);

        when(storage.countAllByUserAndUploadNameAndStatus(any(User.class), any(String.class), any(Status.class))).thenReturn(0L);
        when(storage.findByUserAndUploadNameAndStatus(any(User.class), any(String.class), any(Status.class))).thenReturn(Optional.ofNullable(fileEntityMock));
        when(fileService.save(fileEntityMock)).thenReturn(fileEntityMock);

        doAnswer(invocation -> {
            Object arg = invocation.getArgument(0);
            assertEquals(SAVE_TO_FOLDER_NEW_FILE_NAME, arg);
            return null;
        }).when(fileEntityMock).setUploadName(any(String.class));

        fileService.updateFileUploadName(userMock, TEST_FILENAME, SAVE_TO_FOLDER_NEW_FILE_NAME);
    }

    @Test
    @Order(6)
    void save() {
        FileEntity expected = mock(FileEntity.class);
        when(storage.save(expected)).thenReturn(expected);
        FileEntity actual = fileService.save(expected);

        assertEquals(expected, actual);

    }

    @Test
    @Order(7)
    void isDuplicatedFileName_shouldReturnFalse() {

        User userMock = mock(User.class);

        when(storage.countAllByUserAndUploadNameAndStatus(any(User.class), any(String.class), any(Status.class))).thenReturn(0L);

        boolean actual = fileService.isDuplicatedFileName(userMock, TEST_FILENAME);
        assertFalse(actual);
    }

    @Test
    @Order(8)
    void isDuplicatedFileName_shouldReturnTrue() {
        User userMock = mock(User.class);

        when(storage.countAllByUserAndUploadNameAndStatus(any(User.class), any(String.class), any(Status.class))).thenReturn(1L);

        boolean actual = fileService.isDuplicatedFileName(userMock, TEST_FILENAME);
        assertTrue(actual);
    }

    @Test
    @Order(9)
    void getByUserIdAndFileNameWithActiveStatus_shouldReturnEntity() {
        User userMock = mock(User.class);
        FileEntity expected = mock(FileEntity.class);

        when(storage.findByUserAndUploadNameAndStatus(any(User.class), any(String.class), any(Status.class))).thenReturn(Optional.ofNullable(expected));
        FileEntity actual = fileService.getByUserIdAndFileNameWithActiveStatus(userMock, TEST_FILENAME);

        assertEquals(expected, actual);

    }

    @Test
    @Order(10)
    void getByUserIdAndFileNameWithActiveStatus_shouldThrowException() {
        User userMock = mock(User.class);

        when(storage.findByUserAndUploadNameAndStatus(any(User.class), any(String.class), any(Status.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> {
                    fileService.getByUserIdAndFileNameWithActiveStatus(userMock, TEST_FILENAME);
                });
    }

    @Test
    @Order(11)
    void isAllowableFileType_shouldReturnTrue() {
        String fileExtension = ".png";
        Set allowTypes = new HashSet<>();
        when(config.getAllowTypes()).thenReturn(allowTypes);

        assertTrue(fileService.isAllowableFileType(fileExtension));
    }

    @Test
    @Order(12)
    void isAllowableFileType_shouldReturnFalse() {

        String allowFileExtension = ".png";
        String checkExtension = ".jpg";
        Set allowTypes = new HashSet<>();
        allowTypes.add(allowFileExtension);
        when(config.getAllowTypes()).thenReturn(allowTypes);

        assertFalse(fileService.isAllowableFileType(checkExtension));

    }

    @Test
    @Order(13)
    void generateUniqueFileName() throws ParseException {

        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");

        String fileName = fileService.generateUniqueFileName();

        String uniqueDateString = fileName.substring(fileName.lastIndexOf('-') + 1);
        String uuidString = fileName.replace("-" + uniqueDateString, "");

        UUID uuidFromString = UUID.fromString(uuidString);
        Date dateFromString = ft.parse(uniqueDateString);

        assertInstanceOf(Date.class, dateFromString);
        assertInstanceOf(UUID.class, uuidFromString);
    }

    @Test
    @Order(14)
    void generateFilePathForUserUploadFile() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(TEST_USER_ID);
        when(config.getUploadFolder()).thenReturn(TEST_UPLOAD_FOLDER);

        Path actual = fileService.generateFilePathForUserUploadFile(user);

        assertTrue(actual.toString().contains(TEST_UPLOAD_FOLDER));
        assertTrue(actual.toString().contains(TEST_USER_ID.toString()));

    }

    @Test
    @Order(15)
    void fileToFolder() {
        User userMock = mock(User.class);
        when(userMock.getId()).thenReturn(TEST_USER_ID);

        FileEntity fileEntityMock = mock(FileEntity.class);
        when(fileEntityMock.getName()).thenReturn(TEST_FILENAME);
        when(fileEntityMock.getBytes()).thenReturn(TEST_FILE_DATA);

        when(config.getUploadFolder()).thenReturn(TEST_UPLOAD_FOLDER);

        fileService.fileToFolder(fileEntityMock, userMock);
        File actual = new File( TEST_UPLOAD_FOLDER + "/" + TEST_USER_ID + "/" + LocalDate.now() + "/" + TEST_FILENAME);

        assertTrue(actual.exists() && actual.isFile());

    }

    @Test
    @Order(16)
    void unlinkFile() {

        File file = new File( TEST_UPLOAD_FOLDER + "/" + TEST_USER_ID + "/" + LocalDate.now() + "/" + TEST_FILENAME);
        if(file.exists() && file.isFile()){

            FileEntity fileEntity = mock(FileEntity.class);
            when(fileEntity.getFileFolder()).thenReturn(file.getPath());
            fileService.unlinkFile(fileEntity);
            boolean actual = file.exists();
            FileUtils.deleteQuietly(new File(TEST_UPLOAD_FOLDER));

            assertFalse(actual);

        }
    }
}