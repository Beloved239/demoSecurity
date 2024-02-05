package com.demoProject.repository;

import com.demoProject.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity,String> {
//    Boolean findByToken(String jwt);
@Query("select t from TokenEntity t where t.userEntity.id = ?1 and t.expired = false and t.revoked = false")
List<TokenEntity> findAllValidTokensByUserId(String id);


    Optional<TokenEntity> findByToken(String token);

}
