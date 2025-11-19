package com.fateccotia.yvest.repository;

import com.fateccotia.yvest.entity.Token;
import com.fateccotia.yvest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    
    Optional<Token> findByToken(String token);
    
    Optional<Token> findByUserAndExpirationTimeGreaterThan(User user, Long currentTime);
    
    @Query("SELECT t FROM Token t WHERE t.user = :user AND t.expirationTime > :currentTime ORDER BY t.expirationTime DESC")
    Optional<Token> findTopByUserAndExpirationTimeGreaterThanOrderByExpirationTimeDesc(
        @Param("user") User user, 
        @Param("currentTime") Long currentTime);
    
    void deleteByExpirationTimeLessThan(Long currentTime);
}