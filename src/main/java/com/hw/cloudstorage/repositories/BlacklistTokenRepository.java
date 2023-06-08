package com.hw.cloudstorage.repositories;

import com.hw.cloudstorage.model.entity.BlockedToken;
import org.springframework.data.repository.CrudRepository;

public interface BlacklistTokenRepository extends CrudRepository<BlockedToken, String> {
}
