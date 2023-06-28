package com.hw.cloudstorage.controllers;

import com.hw.cloudstorage.dto.FileDownloadDto;
import com.hw.cloudstorage.dto.FileEntityDto;
import com.hw.cloudstorage.dto.FileEntityDtoBuilder;
import com.hw.cloudstorage.dto.FileNameDto;
import com.hw.cloudstorage.dto.impl.FileEntityDtoBuilderImpl;
import com.hw.cloudstorage.model.FileEntityNameSize;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.services.impl.FileServiceImpl;
import com.hw.cloudstorage.services.impl.UserServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserFileController {
    private final UserServiceImpl userService;
    private final FileServiceImpl fileService;
    private final FileEntityDtoBuilder fileEntityDtoBuilder;

    @Autowired
    public UserFileController(FileServiceImpl fileService, UserServiceImpl userService, FileEntityDtoBuilderImpl fileEntityDtoBuilder) {
        this.fileService = fileService;
        this.userService = userService;
        this.fileEntityDtoBuilder = fileEntityDtoBuilder;

    }

    @GetMapping(value = "list")
    public List<FileEntityNameSize> getUserFilesList(@Valid @RequestParam(name = "limit") int limit, Principal principal) {
        return fileService.allFilesNameAndSizeByUserAndStatusWithLimit(userService.getUser(principal), Status.ACTIVE, limit);
    }

    @GetMapping("file")
    public ResponseEntity<?> getUserFile(@Valid @RequestParam String filename, Principal principal) throws IOException {

        FileEntity fileEntity = fileService.getByUserIdAndFileNameWithActiveStatus(userService.getUser(principal), filename);

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

        return new ResponseEntity<>(fileDownloadDto.getInputStreamResource(), fileDownloadDto.getHeaders(), HttpStatus.OK);
    }

    @PostMapping("file")
    public void uploadFile(@Valid @RequestParam String filename, @RequestPart MultipartFile file, Principal principal) throws IOException {

        User user = userService.getUser(principal);

        if (fileService.isDuplicatedFileName(user, filename)) {
            throw new IllegalArgumentException("File with same name has already been uploaded");
        }

        String fileExtension = FilenameUtils.getExtension(filename);

        if (!fileService.isAllowableFileType(fileExtension)) {
            throw new IllegalArgumentException("File type is not allowed");
        }

        String storageFilename = fileService.generateUniqueFileName() + "." + fileExtension;

        FileEntityDto fileEntityDto = FileEntityDto.builder()
                .filename(storageFilename)
                .uploadName(filename)
                .fileExtension(fileExtension)
                .userId(user.getId())
                .contentType(file.getContentType())
                .size(file.getSize())
                .fileData(file.getBytes())
                .status(Status.ACTIVE)
                .build();

        FileEntity fileEntity = fileEntityDtoBuilder.fromDtoToFileEntity(fileEntityDto);

        fileService.uploadFile(fileEntity, user);

    }

    @PutMapping("file")
    public void updateFile(@Valid @RequestParam String filename, @Valid @RequestBody FileNameDto fileNameDto, Principal principal) {
        fileService.updateFileUploadName(userService.getUser(principal), filename, fileNameDto.getFilename());
    }

    @DeleteMapping("file")
    public void deleteFile(@Valid @RequestParam String filename, Principal principal) {
        fileService.deleteFileByUserAndFilename(userService.getUser(principal), filename);
    }
}
