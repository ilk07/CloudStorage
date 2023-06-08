package com.hw.cloudstorage.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileNameUpdateDto {
    @NotEmpty(message = "Filename can't be empty")
    private String filename;
}
