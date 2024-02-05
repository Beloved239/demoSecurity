package com.demoProject.config;

import com.demoProject.repository.PermissionRepository;
import com.demoProject.repository.RoleRepository;
import com.demoProject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@Component
@Transactional
public class DataInitializerServiceImpl {
    private final RoleRepository roleRepository;
    private final EntityManager entityManager;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
//    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    private Map<String, List<String>> rolePermissionsMap;


    public DataInitializerServiceImpl(RoleRepository roleRepository, EntityManager entityManager, PermissionRepository permissionRepository, PermissionService permissionService, PasswordEncoder passwordEncoder, UserRepository userRepository, OrganizationRepository organizationRepository) {
        this.roleRepository = roleRepository;
        this.entityManager = entityManager;
        this.permissionRepository = permissionRepository;
        this.permissionService = permissionService;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public RoleEntity saveOrUpdate(RoleEntity roleEntity) {
        roleEntity.getPermissions().forEach(entityManager::merge);
        return roleRepository.save(roleEntity);
    }

    @PostConstruct
    public void initialize() {
        this.rolePermissionsMap = allocatePermissionsToRoles();
    }


    @Override
    public void run(java.lang.String... args) {
        createRoleIfNotFound("ROLE_ORGANIZATION_ADMIN");
        createRoleIfNotFound("ROLE_SUPER_ADMIN");
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");
        //        Permission creation coming thru
        permissionService.createPermissionIfNotFound("CREATE_USER", "The \"Create\" operation involves adding new data to the system.");
        permissionService.createPermissionIfNotFound("READ_USER", "The \"Read\" operation involves retrieving existing data from the system.");
        permissionService.createPermissionIfNotFound("UPDATE_USER", "The \"Update\" operation involves modifying existing data in the system.");
        permissionService.createPermissionIfNotFound("DELETE_USER", "The \"Delete\" operation involves removing existing data from the system.");

        // Additional CRUD Permission for assessment
        permissionService.createPermissionIfNotFound("CREATE_ASSESSMENT", "The \"Create\" operation involves adding a new assessment to the system.");
        permissionService.createPermissionIfNotFound("READ_ASSESSMENT", "The \"Read\" operation involves retrieving information about assessments in the system.");
        permissionService.createPermissionIfNotFound("UPDATE_ASSESSMENT", "The \"Update\" operation involves modifying existing assessment data in the system.");
        permissionService.createPermissionIfNotFound("DELETE_ASSESSMENT", "The \"Delete\" operation involves removing an assessment from the system.");

        permissionService.createPermissionIfNotFound("CREATE_QUESTION", "The \"Create\" operation involves adding a new question to the system.");
        permissionService.createPermissionIfNotFound("READ_QUESTION", "The \"Read\" operation involves retrieving information about questions in the system.");
        permissionService.createPermissionIfNotFound("UPDATE_QUESTION", "The \"Update\" operation involves modifying existing question data in the system.");
        permissionService.createPermissionIfNotFound("DELETE_QUESTION", "The \"Delete\" operation involves removing a question from the system.");


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
        Optional<RoleEntity> roleOptional = roleRepository.findByRoleName(roleName);
        if (roleOptional.isEmpty()) {
            RoleEntity role = RoleEntity.builder().roleName(roleName).build();
            roleRepository.save(role);
        }
    }

    private void createSystemUser(SignUpDto request) {
        String username = convertToTitleCase(request.getFirstName()) + convertToTitleCase(request.getLastName()) + generateRandomDigits();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            UserEntity user = UserEntity.builder()
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .username(username)
                    .password(request.getPassword())
                    .usertype(UserType.ADMINISTRATOR)
                    .phoneNumber(request.getPhoneNumber())
                    .middleName(null)
                    .isEnabled(true)
                    .build();
            user.setCreatedBy(username);

            Optional<RoleEntity> roleOptional = roleRepository.findByRoleName("ROLE_SUPER_ADMIN");
            user.setRoleEntities(Collections.singleton(roleOptional.get()));

            userRepository.saveAndFlush(user);
            OrganizationEntity organizationEntity = OrganizationEntity.builder()
                    .name(request.getOrganizationName())
                    .rcNumber(request.getOrganizationRCNumber())
                    .status(Status.ACTIVE)
                    .userEntities(List.of(user))
                    .uniqueCode(0).build();
            organizationEntity.setCreatedBy(username);
            organizationRepository.save(organizationEntity);
        }
    }


    public Map<java.lang.String, List<java.lang.String>> allocatePermissionsToRoles() {
        try {
            // Map role names to associated permissions
            Map<java.lang.String, List<java.lang.String>> rolePermissionsMap = new HashMap<>();
            rolePermissionsMap.put("ROLE_SUPER_ADMIN", Arrays.asList("CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER",
                    "CREATE_ASSESSMENT", "UPDATE_ASSESSMENT", "READ_ASSESSMENT", "DELETE_ASSESSMENT", "CREATE_QUESTION", "READ_QUESTION", "UPDATE_QUESTION", "DELETE_QUESTION"));
            rolePermissionsMap.put("ROLE_ORGANIZATION_ADMIN", Arrays.asList("CREATE_USER", "READ_USER", "UPDATE_USER",
                    "CREATE_ASSESSMENT", "READ_ASSESSMENT", "UPDATE_ASSESSMENT", "CREATE_QUESTION", "READ_QUESTION", "UPDATE_QUESTION"));
            rolePermissionsMap.put("ROLE_ADMIN", Arrays.asList("CREATE_USER", "READ_USER", "UPDATE_USER",
                    "CREATE_ASSESSMENT", "READ_ASSESSMENT", "UPDATE_ASSESSMENT", "CREATE_QUESTION", "READ_QUESTION", "UPDATE_QUESTION"));
            rolePermissionsMap.put("ROLE_USER", Arrays.asList("CREATE_USER", "UPDATE_USER", "READ_USER", "READ_ASSESSMENT"
            ));

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
            RoleEntity role = roleRepository.findByRoleName(roleName).orElse(null);

            // If the role doesn't exist, create it
            if (role == null) {
                role = RoleEntity.builder().roleName(roleName).build();
                roleRepository.save(role);
            }

            // Assign permissions to role that didn't have permission yet
            if (permissions != null && !permissions.isEmpty()) {
                // Initialize the permissions set if it's null
                if (role.getPermissions() == null) {
                    role.setPermissions(new HashSet<>());
                }

                // Filter out existing permissions ensuring there is no duplicate
                RoleEntity finalRole = role;
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
}
