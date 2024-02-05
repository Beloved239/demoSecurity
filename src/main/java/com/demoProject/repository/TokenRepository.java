package com.demoProject.repository;

import com.demoProject.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity,String> {
//    Boolean findByToken(String jwt);
    Optional<TokenEntity> findByToken(String jwt);

}
