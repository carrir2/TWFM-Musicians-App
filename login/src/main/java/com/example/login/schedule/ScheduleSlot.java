package com.example.login.schedule;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * This ScheduleSlot class holds the information relevant
 * for scheduling and seasons. 
 */

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table
public class ScheduleSlot {
    @Id
    @SequenceGenerator(
        name = "sched_sequence",
        sequenceName = "sched_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sched_sequence"
    )
    private Long slotID;
    private String stage;
    private LocalDate date;
    private String time;
    private String status;
    private Long userid;

    /*
     * This is the constructor for the ScheduleSlot class. 
     * Initializes with the date, stage, and time.
     */
    public ScheduleSlot(LocalDate date, String stage, String time){
        this.date = date;
        this.stage = stage;
        this.time = time;
        this.userid = null;
        this.status = "unfilled";
    }

}
