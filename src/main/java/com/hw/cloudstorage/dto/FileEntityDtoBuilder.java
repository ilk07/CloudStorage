package com.hw.cloudstorage.dto;

import com.hw.cloudstorage.model.entity.FileEntity;

public interface FileEntityDtoBuilder {
    FileEntity fromDtoToFileEntity(FileEntityDto fileEntityDto);
}
