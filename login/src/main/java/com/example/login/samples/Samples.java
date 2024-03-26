package com.example.login.samples;

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
 * This Samples Class stores information
 * about a user's samples with external links.
 * Used with details.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Samples {
    @Id
    @SequenceGenerator(
        name = "samp_sequence",
        sequenceName = "samp_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "samp_sequence"
    )
    private Long id;
    private Long userid;
    private String link;


    /*
     * This is the constructor for the Availabilities class. 
     * Initializes User ID and link.
     */
    public Samples(Long userid, String link){
        this.userid = userid;
        this.link = link;
    }
}
