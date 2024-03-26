package com.example.login.registration.token;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.example.login.appuser.AppUser;
import com.example.login.schedule.ScheduleSlot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * This ConfirmationToken Class is used
 * for registering, logging in, and invitations.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConfirmationToken {

    @Id
    @SequenceGenerator(
        name = "confirm_sequence",
        sequenceName = "confirm_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "confirm_sequence"
    )
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime expirationTime;
    private LocalDateTime confirmationTime;
    private String type;

    @ManyToOne
    @JoinColumn(nullable = false, name = "app_user_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(nullable = true, name = "slot_id")
    private ScheduleSlot slot;

    /*
     * This is an alternative constructor used for registering and logging in.
     */
    public ConfirmationToken(String token, LocalDateTime startTime, LocalDateTime expirationTime, String type, AppUser appUser) {
        this.token = token;
        this.startTime = startTime;
        this.expirationTime = expirationTime;
        this.type = type;
        this.appUser = appUser;
    }

    /*
     * This is an alternative constructor used for invitations.
     */
    public ConfirmationToken(String token, LocalDateTime startTime, LocalDateTime expirationTime, String type, AppUser appUser, ScheduleSlot slot) {
        this.token = token;
        this.startTime = startTime;
        this.expirationTime = expirationTime;
        this.type = type;
        this.appUser = appUser;
        this.slot = slot;
    }

}
