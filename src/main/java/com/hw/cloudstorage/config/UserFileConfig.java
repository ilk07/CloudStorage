package com.hw.cloudstorage.config;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties("files")
public class UserFileConfig {
    private Set<String> allowTypes;
    private String uploadFolder;
    private boolean toFolder;
    private boolean toDatabase;
    private boolean removeOnDelete;

    @PostConstruct
    private void postConstruct() {
        Path userFilesUploadFolderPath = Paths.get(uploadFolder);
        if (Files.notExists(userFilesUploadFolderPath)) {
            File sourceFolder = new File("./" + uploadFolder);
            sourceFolder.mkdir();
        }
        if (!toFolder && !toDatabase) {
            this.toDatabase = true;
        }
    }
}
