package com.demoProject.role;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;
    private String name;
    private String description;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}
