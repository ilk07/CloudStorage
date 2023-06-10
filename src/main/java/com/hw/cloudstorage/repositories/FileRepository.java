package com.hw.cloudstorage.repositories;


import com.hw.cloudstorage.model.FileEntityNameSize;
import com.hw.cloudstorage.model.entity.FileEntity;
import com.hw.cloudstorage.model.entity.User;
import com.hw.cloudstorage.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    FileEntity findByUserAndUploadNameAndStatus(User user, String uploadName, Status status);

    int countAllByUserAndUploadNameAndStatus(User user, String uploadName, Status status);

    @Query(nativeQuery = true)
    List<FileEntityNameSize> findUserFileNameSizeByIdAndStatus_Named(Long userId, String status, int limit);
}
