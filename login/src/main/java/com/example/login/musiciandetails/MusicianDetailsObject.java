package com.example.login.musiciandetails;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * This object is used for returning data back
 * to the front end.
 */

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class MusicianDetailsObject {
    
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
    private String[] availabilities;
    private String comments;
    private String[] externalLinks;
    private LocalDateTime dateTime;
    private Integer totalPerformances;
    private Integer totalAvailability;
    private Integer totalSelections;

    /*
     * This is the constructor for the MusicianDetailsObject class. 
     */
    public MusicianDetailsObject(MusicianDetails entry, String[] linksArr, String[] availArr, String[] tempSelectArr){
        this.id = entry.getId();
        this.email = entry.getEmail();
        this.contactName = entry.getContactName();
        this.bandName = entry.getBandName();
        this.setup = entry.getSetup();
        this.lastMinute = entry.getLastMinute();
        this.phoneNumber = entry.getPhoneNumber();
        this.paymentHandle = entry.getPaymentHandle();
        this.gigLength = entry.getGigLength();
        this.gigTime = entry.getGigTime();
        this.comments = entry.getComments();
        this.availabilities = availArr;
        this.externalLinks = linksArr;
        this.totalSelections = tempSelectArr.length;
        this.totalAvailability = availArr.length;
        this.totalPerformances = entry.getTotalPerformances();
        this.dateTime = entry.getDateTime();
    }


}
