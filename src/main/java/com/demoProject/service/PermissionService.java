package com.demoProject.service;

import com.demoProject.repository.PermissionRepository;
import com.demoProject.role.PermissionEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Optional<PermissionEntity> createPermissionIfNotFound(String name, String description) {
        Optional<PermissionEntity> permissionOptional = permissionRepository.findByNameOrDescription(name,description);

        if (!permissionOptional.isPresent()) {
            PermissionEntity permission = PermissionEntity.builder()
                    .name(name)
                    .description(description)
                    .build();
            permissionRepository.save(permission);
            return Optional.of(permission);
        } else {
            return permissionOptional;
        }
    }

}
