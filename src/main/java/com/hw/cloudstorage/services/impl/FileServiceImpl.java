package com.hw.cloudstorage.services.impl;

import com.hw.cloudstorage.config.UserFileConfig;
import com.hw.cloudstorage.dto.FileDownloadDto;
import com.hw.cloudstorage.dto.FileEntityDto;
import com.hw.cloudstorage.dto.FileEntityNameSizeDto;
import com.hw.cloudstorage.dto.impl.FileEntityDtoBuilderImpl;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.repositories.FileRepository;
import com.hw.cloudstorage.services.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository storage;
    private final FileEntityDtoBuilderImpl fileDtoBuilder;
    private final UserFileConfig config;

    @Autowired
    public FileServiceImpl(FileRepository storage, FileEntityDtoBuilderImpl fileDtoBuilder, UserFileConfig config) {
        this.storage = storage;
        this.fileDtoBuilder = fileDtoBuilder;
        this.config = config;
    }

    public void uploadUserFile(User user, String uploadFileName, MultipartFile file) throws IOException {

        String fileExtension = getUploadFileExtension(uploadFileName);
        String storageFilename = generateUniqueFileName() + "." + fileExtension;
        byte[] uploadFileBytes = file.getBytes();

        if (isDuplicatedFileName(user, uploadFileName)) {
            throw new IllegalArgumentException("File with same name has already been uploaded");
        }

        if (config.getAllowTypes().isEmpty() || config.getAllowTypes().stream().anyMatch(fileExtension::equalsIgnoreCase)) { //allow all file types
            FileEntityDto fileEntityDto = FileEntityDto.builder()
                    .filename(storageFilename)
                    .uploadName(uploadFileName)
                    .contentType(file.getContentType())
                    .fileExtension(fileExtension)
                    .size(file.getSize())
                    .userId(user.getId())
                    .build();

            if (config.isToFolder()) {
                Path uploadDir = generateFilePathForUserUploadFile(user);
                Path uploadFilePath = uploadDir.resolve(storageFilename);
                String fullFilePath = uploadFilePath.toString();
                FileUtils.writeByteArrayToFile(new File(fullFilePath), uploadFileBytes);
                fileEntityDto.setFileFolder(fullFilePath);

            }

            if (config.isToDatabase()) {
                fileEntityDto.setFileData(uploadFileBytes);

            }

            fileEntityDto.setStatus(Status.ACTIVE);
            FileEntity fileEntity = fileDtoBuilder.fromDtoToFileEntity(fileEntityDto);
            save(fileEntity);

        } else {
            throw new IllegalArgumentException("File type is not allowed");
        }

    }

    @Override
    public List<FileEntityNameSizeDto> allUserFilesNameSize(User user, Status status, int limit) {
        return storage.findUserFileNameSizeDtoByIdAndStatus_Named(user.getId(), String.valueOf(status), limit);
    }

    public void deleteUserFileByFilename(User user, String filename) {
        FileEntity fileEntity = getByUserIdAndFileNameWithActiveStatus(user, filename);

        if (config.isRemoveOnDelete()) {
            if (fileEntity.getFileFolder() != null) {
                File fileToDelete = FileUtils.getFile(fileEntity.getFileFolder());
                if (fileToDelete.exists()) {
                    File fileFolder = new File(fileToDelete.getParent());
                    FileUtils.deleteQuietly(fileToDelete);
                    if (fileFolder.isDirectory() && fileFolder.list().length == 0) {
                        FileUtils.deleteQuietly(fileFolder);
                    }
                }
            }
            storage.delete(fileEntity);
        } else {
            fileEntity.setStatus(Status.DELETED);
            save(fileEntity);
        }
    }

    @Override
    @Modifying
    public void updateUserFileUploadName(User user, String filename, String newFileName) {

        if (isDuplicatedFileName(user, newFileName)) {
            throw new IllegalArgumentException("Filename " + newFileName + " is busy, update canceled");
        }

        FileEntity file = getByUserIdAndFileNameWithActiveStatus(user, filename);
        file.setUploadName(newFileName);
        save(file);
    }

    @Override
    public void save(FileEntity fileEntity) {
        storage.save(fileEntity);
    }

    public boolean isDuplicatedFileName(User user, String fileName) {
        if (storage.countAllByUserAndUploadNameAndStatus(user, fileName, Status.ACTIVE) > 0) {
            return true;
        }
        return false;
    }

    public FileEntity getByUserIdAndFileNameWithActiveStatus(User user, String filename) {
        Optional<FileEntity> fileEntity = storage.findByUserAndUploadNameAndStatus(user, filename, Status.ACTIVE);
        if (fileEntity.isPresent()) {
            return fileEntity.get();
        } else {
            throw new IllegalArgumentException("File " + filename + " not found");
        }
    }

    public String getUploadFileExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }

    public String generateUniqueFileName() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
        return ft.format(dNow);
    }

    public Path generateFilePathForUserUploadFile(User user) {
        return Paths.get(config.getUploadFolder() + "/" + user.getId() + "/" + LocalDate.now());
    }

    public FileDownloadDto prepareFileToDownload(User user, String filename) throws IOException {

        FileEntity fileEntity = getByUserIdAndFileNameWithActiveStatus(user, filename);

        FileDownloadDto fileDownloadDto = new FileDownloadDto();

        if (fileEntity.getFileFolder() != null && !fileEntity.getFileFolder().isEmpty() && new File(fileEntity.getFileFolder()).isFile()) {
            String filePath = fileEntity.getFileFolder();
            fileDownloadDto.getHeaders().setContentLength(Files.size(Paths.get(filePath)));
            fileDownloadDto.setInputStreamResource(new InputStreamResource(new FileInputStream(filePath)));

        } else {
            if (fileEntity.getBytes().length > 0) {
                fileDownloadDto.getHeaders().setContentLength(fileEntity.getSize());
                fileDownloadDto.setInputStreamResource(
                        new InputStreamResource(new ByteArrayInputStream(fileEntity.getBytes()))
                );
            }
        }

        return fileDownloadDto;

    }
}
