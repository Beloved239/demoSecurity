package com.demoProject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;
    @Column(name = "token", length = 50000)
    private String token;
    private TokenType tokenType;
    private Boolean expired;
    private Boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userEntity;
}
