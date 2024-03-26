package com.example.login.availabilities;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * This Availabilities Class stores information
 * about a user's availbility with a date and status.
 * Used with details and scheduler.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Availabilities {
    @Id
    @SequenceGenerator(
        name = "avail_sequence",
        sequenceName = "avail_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "avail_sequence"
    )
    private Long id;
    private Long userid;
    private LocalDate date;
    private String statusAM;
    private String statusPM;

    /*
     * This is the constructor for the Availabilities class. 
     * Initializes userid, date, morning and afternoon statuses.
     */
    public Availabilities(Long userid, LocalDate date){
        this.userid = userid;
        this.date = date;
        this.statusAM = "none";
        this.statusPM = "none";
    }
}
