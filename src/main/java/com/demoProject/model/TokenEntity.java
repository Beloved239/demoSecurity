package com.demoProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "token", length = 50000)
    private String token;
    private TokenType tokenType;
    private Boolean expired;
    private Boolean revoked;

    @ManyToOne
    @JoinColumn( name = "user_id")
    private User userEntity;
}
//    @Id
//    @GeneratedValue(generator="system-uuid")
//    @GenericGenerator(name="system-uuid", strategy = "uuid")
//    private String id = UUID.randomUUID().toString();
