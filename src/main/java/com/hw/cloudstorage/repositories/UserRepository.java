package com.hw.cloudstorage.repositories;


import com.hw.cloudstorage.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
   User findByUsername(String username);

}
