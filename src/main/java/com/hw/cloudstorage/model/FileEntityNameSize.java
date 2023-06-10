package com.hw.cloudstorage.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FileEntityNameSize {
    private String filename;
    private Long size;
}
