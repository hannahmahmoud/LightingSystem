package com.lightingsystem.lightingsystem.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.lightingsystem.lightingsystem.Model.AuthUser;


 
@Repository
@EnableJpaRepositories
// dah interface ana b3mlo ashan y3mli y- provide funcs 
public interface userRepo extends JpaRepository<AuthUser,Long>
// long hwa type of id , authuser hwa asm file bat3 model 
 {
    // Check if a user with a given email already exists
    boolean existsByEmail(String email);

 
    // Find a user by email (used for login or forgot password)
   Optional<AuthUser> findByEmail(String email);
  




    
} 
