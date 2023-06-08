package com.hw.cloudstorage.model.entity;

import com.hw.cloudstorage.dto.FileEntityNameSizeDto;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@NamedNativeQuery(
        name = "FileEntity.findUserFileNameSizeDtoByIdAndStatus_Named",
        query = "SELECT uf.upload_name as filename, uf.size FROM user_files as uf WHERE uf.user_id = :userId and status = :status limit :limit",
        resultClass = FileEntityNameSizeDto.class,
        resultSetMapping = "Mapping.FileEntityNameSizeDto")
@SqlResultSetMapping(
        name = "Mapping.FileEntityNameSizeDto",
        classes = @ConstructorResult(targetClass = FileEntityNameSizeDto.class,
                columns = {
                        @ColumnResult(name = "filename", type = String.class),
                        @ColumnResult(name = "size", type = Long.class)
                }))

@Entity
@Table(name = "user_files")
public class FileEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "upload_name")
    private String uploadName;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "file_folder")
    private String fileFolder;

    @Column(name = "size")
    private Long size;

    @Column(name = "content_type")
    private String contentType;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "file_content")
    private byte[] bytes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

}
