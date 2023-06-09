package com.hw.cloudstorage.controllers;

import com.hw.cloudstorage.dto.FileDownloadDto;
import com.hw.cloudstorage.dto.FileEntityNameSizeDto;
import com.hw.cloudstorage.dto.FileNameUpdateDto;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import com.hw.cloudstorage.services.UserService;
import com.hw.cloudstorage.services.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class UserFileController {
    private final UserService userService;
    private final FileServiceImpl fileService;

    @Autowired
    public UserFileController(FileServiceImpl fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping(value = "list")
    public List<FileEntityNameSizeDto> getUserFilesList(@RequestParam(required = false) Optional<Integer> limit, Principal principal) {
        return fileService.allUserFilesNameSize(getUser(principal), Status.ACTIVE, limit.orElse(5));
    }

    @GetMapping("file")
    public ResponseEntity<?> getUserFile(@RequestParam String filename, Principal principal) throws IOException {

        FileDownloadDto fileDownloadDto = fileService.prepareFileToDownload(getUser(principal), filename);

        if (fileDownloadDto == null) {
            throw new FileNotFoundException("File " + filename + "not found");
        }

        return new ResponseEntity(fileDownloadDto.getInputStreamResource(), fileDownloadDto.getHeaders(), HttpStatus.OK);
    }

    @PostMapping("file")
    public void uploadFile(@Valid @RequestParam String filename, @RequestPart MultipartFile file, Principal principal) throws IOException {
        fileService.uploadUserFile(getUser(principal), filename, file);
    }

    @PutMapping("file")
    public void updateFile(@Valid @RequestParam String filename, @Valid @RequestBody FileNameUpdateDto fileNameUpdateDto, Principal principal) {
        fileService.updateUserFileUploadName(getUser(principal), filename, fileNameUpdateDto.getFilename());
    }

    @DeleteMapping("file")
    public void deleteFile(@Valid @RequestParam String filename, Principal principal) {
        fileService.deleteUserFileByFilename(getUser(principal), filename);
    }

    public User getUser(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found : " + principal.getName());
        }
        return user;
    }
}
