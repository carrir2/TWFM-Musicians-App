package com.example.login.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/*
 * Repository for AppUser. Used to access the AppUser Database
 * and run SQL queries.
 */

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<AppUser, Long>{
    
    /*
     * Returns Optional<AppUser> through Email.
     */
    Optional<AppUser> findByEmail(String email);
    
    /*
     * Returns AppUser through Email.
     */
    AppUser findUserByEmail(String email);

     /*
     * Returns AppUser through User ID.
     */   
    AppUser findUserById(Long id);

    /*
     * Changes Enable status to true of AppUser through Email. 
     */
    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);
}
