package com.example.login.musiciandetails;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * This MusicianDetails class holds the information submitted by users
 * found in the application form. 
 */

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table
public class MusicianDetails {

    @Id
    private Long id;
    private String email;
    private String contactName;
    private String bandName;
    private String setup;
    private String lastMinute;
    private String phoneNumber;
    private String paymentHandle;
    private String gigLength;
    private String gigTime;
    private String comments;
    private LocalDateTime dateTime;
    private Integer totalPerformances = 0;
    private Integer totalAvailability = 0;
    private Integer totalSelections = 0;

    /*
     * This is the constructor for the MusicianDetails class. 
     * Initializes with the details found in a user submitted application form.
     */
    public MusicianDetails(String email, String contactName, String bandName, String setup, 
        String lastMinute, String phoneNumber, String paymentHandle, String gigLength, String gigTime, 
        String comments) {

        this.email = email;
        this.contactName = contactName;
        this.bandName = bandName;
        this.setup = setup;
        this.lastMinute = lastMinute;
        this.phoneNumber = phoneNumber;
        this.paymentHandle = paymentHandle;
        this.gigLength = gigLength;
        this.gigTime = gigTime;
        this.comments = comments;
        this.dateTime = LocalDateTime.now();
    }

    /*
     * This is an alternative constructor for the MusicianDetails class. 
     * Initializes with the details found in AppUser
     */
    public MusicianDetails(String email, String contactName) {
        this.email = email;
        this.contactName = contactName;
        this.dateTime = LocalDateTime.now();
    }

    /*
     * Updates the MusicianDetails for a user.
     */
    public void updateMusicianDetails(String email, String contactName, String bandName, String setup, 
    String lastMinute, String phoneNumber, String paymentHandle, String gigLength, String gigTime, 
    String comments) {
        
        this.email = email;
        this.contactName = contactName;
        this.bandName = bandName;
        this.setup = setup;
        this.lastMinute = lastMinute;
        this.phoneNumber = phoneNumber;
        this.paymentHandle = paymentHandle;
        this.gigLength = gigLength;
        this.gigTime = gigTime;
        this.comments = comments;
        this.dateTime = LocalDateTime.now();
    }

    
    
}
