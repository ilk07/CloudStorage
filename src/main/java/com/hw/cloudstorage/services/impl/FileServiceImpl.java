package com.hw.cloudstorage.services.impl;

import com.hw.cloudstorage.config.UserFileConfig;
import com.hw.cloudstorage.exceptions.UploadFileToFolderException;
import com.hw.cloudstorage.model.FileEntityNameSize;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.repositories.FileRepository;
import com.hw.cloudstorage.services.FileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository storage;
    private final UserFileConfig config;

    @Autowired
    public FileServiceImpl(FileRepository storage, UserFileConfig config) {
        this.storage = storage;
        this.config = config;
    }

    @Override
    public void uploadFile(FileEntity fileEntity, User user) {
        if (config.isToFolder()) {
            fileEntity = fileToFolder(fileEntity, user);
            if (!config.isToDatabase()) {
                fileEntity.setBytes(null);
            }
        }
        save(fileEntity);
    }

    @Override
    public List<FileEntityNameSize> allFilesNameAndSizeByUserAndStatusWithLimit(User user, Status status, int limit) {
        return storage.findUserFileNameSizeByIdAndStatus_Named(user.getId(), String.valueOf(status), limit);
    }

    public void deleteFileByUserAndFilename(User user, String filename) {
        FileEntity fileEntity = getByUserIdAndFileNameWithActiveStatus(user, filename);

        if (config.isRemoveOnDelete()) {
            unlinkFile(fileEntity);
            storage.delete(fileEntity);
        } else {
            fileEntity.setStatus(Status.DELETED);
            save(fileEntity);
        }
    }

    @Override
    @Modifying
    public void updateFileUploadName(User user, String filename, String newFileName) {

        if (isDuplicatedFileName(user, newFileName)) {
            throw new IllegalArgumentException("Filename " + newFileName + " is busy, update canceled");
        }

        FileEntity file = getByUserIdAndFileNameWithActiveStatus(user, filename);
        file.setUploadName(newFileName);
        save(file);
    }

    @Override
    public FileEntity save(FileEntity fileEntity) {
        return storage.save(fileEntity);
    }

    public boolean isDuplicatedFileName(User user, String fileName) {
        return storage.countAllByUserAndUploadNameAndStatus(user, fileName, Status.ACTIVE) > 0;
    }

    public FileEntity getByUserIdAndFileNameWithActiveStatus(User user, String filename) {
        Optional<FileEntity> fileEntity = storage.findByUserAndUploadNameAndStatus(user, filename, Status.ACTIVE);
        if (fileEntity.isPresent()) {
            return fileEntity.get();
        }

        throw new IllegalArgumentException("File " + filename + " not found");
    }


    public boolean isAllowableFileType(String fileExtension) {
        return config.getAllowTypes().isEmpty() || config.getAllowTypes().stream().anyMatch(fileExtension::equalsIgnoreCase);
    }

    public String generateUniqueFileName() {

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
        return UUID.randomUUID() + "-" +ft.format(dNow);
    }

    public Path generateFilePathForUserUploadFile(User user) {
        return Paths.get(config.getUploadFolder() + "/" + user.getId() + "/" + LocalDate.now());
    }

    public void unlinkFile(FileEntity fileEntity){
        if (fileEntity.getFileFolder() != null) {
            File fileToDelete = FileUtils.getFile(fileEntity.getFileFolder());
            if (fileToDelete.exists()) {
                File fileFolder = new File(fileToDelete.getParent());
                FileUtils.deleteQuietly(fileToDelete);
                if (fileFolder.isDirectory() && Objects.requireNonNull(fileFolder.list()).length == 0) {
                    FileUtils.deleteQuietly(fileFolder);
                }
            }
        }
    }

    public FileEntity fileToFolder(FileEntity fileEntity, User user) {
        try {
            Path uploadDir = generateFilePathForUserUploadFile(user);
            Path uploadFilePath = uploadDir.resolve(fileEntity.getName());
            String fullFilePath = uploadFilePath.toString();
            FileUtils.writeByteArrayToFile(new File(fullFilePath), fileEntity.getBytes());
            fileEntity.setFileFolder(fullFilePath);
        }  catch (IOException e) {
            throw new UploadFileToFolderException("File upload to folder failed");
        }
        return fileEntity;
    }
}
