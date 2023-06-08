package com.hw.cloudstorage.services;

import com.hw.cloudstorage.dto.FileEntityNameSizeDto;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;

import java.io.FileNotFoundException;
import java.util.List;

public interface FileService {
    void save(FileEntity fileEntity);

    void deleteUserFileByFilename(User user, String filename) throws FileNotFoundException;

    void updateUserFileUploadName(User user, String filename, String newFileName);

    List<FileEntityNameSizeDto> allUserFilesNameSize(User user, Status status, int limit);
}
