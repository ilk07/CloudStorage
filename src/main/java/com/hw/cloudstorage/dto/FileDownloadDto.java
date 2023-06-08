package com.hw.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadDto {
    private HttpHeaders headers = new HttpHeaders();
    private InputStreamResource inputStreamResource;
}