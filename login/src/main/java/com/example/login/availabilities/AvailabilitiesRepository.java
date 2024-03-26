package com.example.login.availabilities;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
 * Repository for Availabilities. Used to access the Availabilities Database
 * and run SQL queries.
 */

@Repository
@Transactional
public interface AvailabilitiesRepository extends JpaRepository<Availabilities, Long>{

    /*
     * Deletes all availabilities by User ID.
     */
    @Modifying
    @Query(value = "DELETE FROM Availabilities a WHERE a.userid = :userid AND a.statuspm= 'none' AND a.statusam='none'", nativeQuery = true)
    int deleteByUserid(@Param("userid") Long userid);

    /*
     * Finds an availability by Date and User ID.
     */
    @Query(value = "SELECT a.* FROM Availabilities a WHERE a.userid = :userid AND DATE(a.date) = :date", nativeQuery = true)
    Availabilities findByDateAndId(@Param("date") LocalDate date, @Param("userid") Long userid);

    /*
     * Finds all availabilities of a User through User Id.
     */
    @Query(value = "SELECT a.* FROM Availabilities a WHERE a.userid = :userid AND DATE(a.date) >= :date", nativeQuery = true)
    List<Availabilities> getAllAvailByUserid(@Param("userid") Long userid, @Param("date") LocalDate date);
}
