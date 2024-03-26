package com.example.login.schedule;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*
 * Repository for ScheduleSlot. Used to access the ScheduleSlot Database
 * and run SQL queries.
 */

@Repository
@Transactional
public interface ScheduleRepository extends JpaRepository<ScheduleSlot, Long>{
   
    /*
     * Returns ScheduleSlot through Slot ID.
     */    
    ScheduleSlot findSlotBySlotID(Long slotID);

    /*
     * Returns all ScheduleSlotData with additional associated musicians 
     */  
    @Query(value = "SELECT s.slotid AS slotid, s.stage as stage, s.date as date, s.time as time, s.status as status, s.userid as userid, d.band_name as bandName FROM Schedule_Slot s LEFT JOIN Musician_Details d ON d.id=s.userid", nativeQuery = true)
    List<ScheduleMusicianData> findAllSchedule();
    
    /*
     * Returns all ScheduleSlotData with additional associated musicians 
     */      
    @Query(value = "SELECT s.slotid AS slotid, s.stage as stage, s.date as date, s.time as time, s.status as status, s.userid as userid, d.band_name as bandName FROM Schedule_Slot s LEFT JOIN Musician_Details d ON d.id=s.userid WHERE DATE(s.date) = :date ORDER BY s.stage, s.time", nativeQuery = true)
    List<ScheduleMusicianData> findAllByDate(@Param("date") LocalDate date);

    /*
     * Returns a list of a ScheduleSlots that have a specified user selected
     * through User ID. 
     */   
    @Query(value = "SELECT s.* FROM Schedule_Slot s WHERE s.userid = :userid", nativeQuery = true)
    List<ScheduleSlot> findAllById(@Param("userid") Long userid);

    /*
     * Deletes ScheduleSlots that are between a start date and end date.
     */   
    @Modifying
    @Query(value = "DELETE FROM Schedule_Slot WHERE DATE(date) >= :sdate AND DATE(date) <= :edate", nativeQuery = true)
    int deleteBySeason(@Param("sdate") LocalDate sdate, @Param("edate") LocalDate edate);
    

}