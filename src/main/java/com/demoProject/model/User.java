package com.demoProject.model;

import com.demoProject.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Long id;
//    @Id
//    @GeneratedValue(generator="system-uuid")
//    @GenericGenerator(name="system-uuid", strategy = "uuid")
//    private String id = UUID.randomUUID().toString();
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String department;
    private String phoneNumber;
    private String password;
    private Boolean isEnabled;
    private String username;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id"))
    private Set<Role> roleEntities = new HashSet<>();
}
