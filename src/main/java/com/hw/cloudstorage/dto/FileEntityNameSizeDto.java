package com.hw.cloudstorage.dto;

import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FileEntityNameSizeDto {
    private String filename;
    private Long size;
}
