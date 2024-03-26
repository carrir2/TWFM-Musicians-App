package com.example.login.samples;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
 * Repository for Samples. Used to access the Samples Database
 * and run SQL queries.
 */

@Repository
@Transactional
public interface SamplesRepository extends JpaRepository<Samples, Long>{
    
    /*
     * Returns a list of samples from a user through User ID.
     */    
    @Query(value = "SELECT * FROM Samples a WHERE a.userid = :userid", nativeQuery = true)
    List<Samples> getByUserid(@Param("userid") Long userid);

    /*
     * Deletes all samples from a user through User ID.
     */  
    @Modifying
    @Query(value = "DELETE FROM Samples a WHERE a.userid = :userid")
    int deleteByUserid(@Param("userid") Long userid);
}
