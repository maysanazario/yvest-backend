package com.fateccotia.yvest.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fateccotia.yvest.entity.User;

@Repository
public interface UserRepository 
            extends CrudRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}