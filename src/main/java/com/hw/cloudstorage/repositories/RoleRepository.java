package com.hw.cloudstorage.repositories;

import com.hw.cloudstorage.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
