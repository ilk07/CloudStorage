package com.hw.cloudstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileNameDto {
    @NotEmpty(message = "Filename can't be empty")
    private String filename;
}
