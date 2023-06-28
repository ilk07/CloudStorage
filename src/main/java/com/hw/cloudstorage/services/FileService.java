package com.hw.cloudstorage.services;


import com.hw.cloudstorage.model.FileEntityNameSize;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;

import java.io.FileNotFoundException;
import java.util.List;

public interface FileService {
    FileEntity save(FileEntity fileEntity);

    void uploadFile(FileEntity fileEntity, User user);


    void deleteFileByUserAndFilename(User user, String filename) throws FileNotFoundException;

    void updateFileUploadName(User user, String filename, String newFileName);

    List<FileEntityNameSize> allFilesNameAndSizeByUserAndStatusWithLimit(User user, Status status, int limit);

}
