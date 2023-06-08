package com.hw.cloudstorage.dto;


import com.hw.cloudstorage.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FileEntityDto {

    @NotEmpty(message = "Filename can't be empty")
    private String filename;

    @NotEmpty(message = "Filename is empty")
    private final String uploadName;

    @NotEmpty(message = "File extension is empty")
    private String fileExtension;

    private String fileFolder;

    @NotNull(message = "File size is null")
    private final Long size;

    @NotEmpty(message = "Unknown content type")
    private String contentType;

    @NotNull(message = "File is empty")
    private byte[] fileData;

    @NotNull(message = "Unknown user")
    private Long userId;

    private Status status;
}







