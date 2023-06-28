package com.hw.cloudstorage.dto.impl;

import com.hw.cloudstorage.dto.FileEntityDto;
import com.hw.cloudstorage.dto.FileEntityDtoBuilder;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileEntityDtoBuilderImpl implements FileEntityDtoBuilder {
    @Autowired
    UserServiceImpl userService;

    @Override
    public FileEntity fromDtoToFileEntity(FileEntityDto fileEntityDto) {
        FileEntity fileEntity = FileEntity.builder()
                .bytes(fileEntityDto.getFileData())
                .contentType(fileEntityDto.getContentType())
                .fileExtension(fileEntityDto.getFileExtension())
                .fileFolder(fileEntityDto.getFileFolder())
                .size(fileEntityDto.getSize())
                .uploadName(fileEntityDto.getUploadName())
                .name(fileEntityDto.getFilename())
                .user(userService.findById(fileEntityDto.getUserId()))
                .build();
        fileEntity.setStatus(fileEntityDto.getStatus());
        return fileEntity;
    }
}
