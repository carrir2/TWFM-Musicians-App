package com.example.login.musiciandetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * Repository for MusicianDetails. Used to access the MusicianDetails Database
 * and run SQL queries.
 */

@Repository
public interface MusicianDetailsRepository extends JpaRepository<MusicianDetails, Long>{
    
    /*
     * Returns Optional<MusicianDetails> through id.
     */
    Optional<MusicianDetails> findMusicianById(Long id);

    /*
     * Returns MusicianDetails through Email.
     */
    MusicianDetails getMusicianByEmail(String email);

    /*
     * Returns MusicianDetails through User ID.
     */
    MusicianDetails getMusicianById(Long id);
    
    /*
     * Returns a queue of musicians and their details for a specified date, sorted by selections, performances, availabilities, and submission time.
     */ 
    @Query(value = "SELECT d.* FROM availabilities a LEFT JOIN Musician_Details d ON a.userid=d.id WHERE DATE(a.date) = :date ORDER BY total_performances ASC, total_availability ASC, date_time ASC", nativeQuery = true)
    List<MusicianDetails> getMusicianQueue(@Param("date") LocalDate date);

    /*
     * Returns a queue of musicians and their details for a specified date and morning time, sorted by selections, performances, availabilities, and submission time.
     */ 
    @Query(value = "SELECT d.* FROM availabilities a LEFT JOIN Musician_Details d ON a.userid=d.id WHERE DATE(a.date) = :date AND (d.gig_time= :time OR d.gig_time= 'both') AND (a.statusam = 'none' OR a.statusam = 'selected') ORDER BY statusam DESC, total_performances ASC, total_selections ASC, total_availability ASC, date_time ASC", nativeQuery = true)
    List<MusicianDetails> getMusicianQueueAM(@Param("date") LocalDate date, @Param("time") String time);

    /*
     * Returns a queue of musicians and their details for a specified date and afternoon time, sorted by selections, performances, availabilities, and submission time.
     */ 
    @Query(value = "SELECT d.* FROM availabilities a LEFT JOIN Musician_Details d ON a.userid=d.id WHERE DATE(a.date) = :date AND (d.gig_time= :time OR d.gig_time= 'both') AND (a.statuspm = 'none' OR a.statuspm = 'selected') ORDER BY statuspm DESC, total_performances ASC, total_selections ASC, total_availability ASC, date_time ASC", nativeQuery = true)
    List<MusicianDetails> getMusicianQueuePM(@Param("date") LocalDate date, @Param("time") String time);

    /*
     * Returns all musician details from the database.
     */ 
    @Query(value = "SELECT * FROM musician_details", nativeQuery = true)
    List<MusicianDetails> getAllMusicians();
}
