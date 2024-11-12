package com.example.printmatic.repository;

import com.example.printmatic.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepositoty extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
}
