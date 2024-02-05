package com.demoProject.config;

import com.demoProject.dto.SignUpDto;
import com.demoProject.model.User;
import com.demoProject.repository.PermissionRepository;
import com.demoProject.repository.RoleRepository;
import com.demoProject.repository.UserRepository;
import com.demoProject.role.PermissionEntity;
import com.demoProject.role.Role;
import com.demoProject.service.PermissionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class DataInitializerServiceImpl implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final EntityManager entityManager;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
//    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    private Map<String, List<String>> rolePermissionsMap;


    public DataInitializerServiceImpl(RoleRepository roleRepository, EntityManager entityManager, PermissionRepository permissionRepository, PermissionService permissionService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.entityManager = entityManager;
        this.permissionRepository = permissionRepository;
        this.permissionService = permissionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public Role saveOrUpdate(Role roleEntity) {
        roleEntity.getPermissions().forEach(entityManager::merge);
        return roleRepository.save(roleEntity);
    }

    @PostConstruct
    public void initialize() {
        this.rolePermissionsMap = allocatePermissionsToRoles();
    }


    @Override
    public void run(java.lang.String... args) {
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");
        //        Permission creation coming thru
        permissionService.createPermissionIfNotFound("CREATE_USER", "The \"Create\" operation involves adding new data to the system.");
        permissionService.createPermissionIfNotFound("READ_USER", "The \"Read\" operation involves retrieving existing data from the system.");
        permissionService.createPermissionIfNotFound("UPDATE_USER", "The \"Update\" operation involves modifying existing data in the system.");
        permissionService.createPermissionIfNotFound("DELETE_USER", "The \"Delete\" operation involves removing existing data from the system.");


        rolePermissionsMap = allocatePermissionsToRoles();

        createSystemUser(SignUpDto.builder()
                .lastName("portal")
                .organizationName("I-Academy")
                .organizationRCNumber("123456")
                .email("java@i-academy.org")
                .firstName("application")
                .phoneNumber("08186543207")
                .password(passwordEncoder.encode("123456Abc!"))
                .build());
    }


    private void createRoleIfNotFound(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByRoleName(roleName);
        if (!roleOptional.isPresent()) {
            Role role = Role.builder().roleName(roleName).build();
            roleRepository.save(role);
        }
    }

    private void createSystemUser(SignUpDto request) {
        String username = convertToTitleCase(request.getFirstName()) + convertToTitleCase(request.getLastName()) + generateRandomDigits();
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (!optionalUser.isPresent()) {
            User user = User.builder()
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .username(username)
                    .password(request.getPassword())
//                    .usertype(UserType.ADMINISTRATOR)
                    .phoneNumber(request.getPhoneNumber())
                    .middleName(null)
                    .isEnabled(true)
                    .build();
//            user.setCreatedBy(username);

            Optional<Role> roleOptional = roleRepository.findByRoleName("ROLE_SUPER_ADMIN");
            user.setRoleEntities(Collections.singleton(roleOptional.get()));

            userRepository.saveAndFlush(user);
//
        }
    }


    public Map<java.lang.String, List<java.lang.String>> allocatePermissionsToRoles() {
        try {
            // Map role names to associated permissions
            Map<java.lang.String, List<java.lang.String>> rolePermissionsMap = new HashMap<>();
            rolePermissionsMap.put("ROLE_HR", Arrays.asList("CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER"));

            rolePermissionsMap.put("ROLE_ADMIN", Arrays.asList("CREATE_USER", "READ_USER", "UPDATE_USER"));
            rolePermissionsMap.put("ROLE_USER", Arrays.asList("CREATE_USER", "UPDATE_USER", "READ_USER"));

            for (Map.Entry<java.lang.String, List<java.lang.String>> entry : rolePermissionsMap.entrySet()) {
                List<PermissionEntity> permissions = entry.getValue().stream()
                        .map(permissionName -> permissionService.createPermissionIfNotFound(permissionName, "Description for " + permissionName))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

                createRoleAndAssignPermissions(entry.getKey(), permissions);
            }

            return rolePermissionsMap;  // Return the populated map
        } catch (Exception e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            return Collections.emptyMap();  // Return an empty map or handle the error case
        }
    }


    private void createRoleAndAssignPermissions(java.lang.String roleName, List<PermissionEntity> permissions) {
        try {
            // Check if the role already exists
            Role role = roleRepository.findByRoleName(roleName).orElse(null);

            // If the role doesn't exist, create it
            if (role == null) {
                role = Role.builder().roleName(roleName).build();
                roleRepository.save(role);
            }

            // Assign permissions to role that didn't have permission yet
            if (permissions != null && !permissions.isEmpty()) {
                // Initialize the permissions set if it's null
                if (role.getPermissions() == null) {
                    role.setPermissions(new HashSet<>());
                }

                // Filter out existing permissions ensuring there is no duplicate
                Role finalRole = role;
                List<PermissionEntity> newPermissions = permissions.stream()
                        .filter(permission -> !finalRole.getPermissions().contains(permission))
                        .collect(Collectors.toList());

                // Add only new permissions
                role.getPermissions().addAll(newPermissions);
                roleRepository.save(role);
            }
        } catch (Exception e) {
            // log the exception
            e.printStackTrace();
        }
    }

    public static String convertToTitleCase(String inputCase) {
        StringBuilder sb = new StringBuilder();
        sb.append(inputCase.substring(0, 1).toUpperCase()).append(inputCase.substring(1).toLowerCase());
        return String.valueOf(sb);
    }
    public static String generateRandomDigits() {
        int randomDigits = 1000 + new SecureRandom().nextInt(9000);
        return String.valueOf(randomDigits);
    }
}
