package com.demoProject.repository;

import com.demoProject.role.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity,Long> {
    Optional<PermissionEntity> findByNameOrDescription(String name, String description);

    boolean existsByNameOrDescription(String permissionName, String permissionName1);
}
