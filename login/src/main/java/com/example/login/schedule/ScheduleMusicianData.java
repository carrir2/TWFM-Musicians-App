package com.example.login.schedule;

import java.time.LocalDate;

/*
 * This interface is used for returning data back
 * to the front end.
 */

public interface ScheduleMusicianData {

    Long getSlotid();
    String getStage();
    LocalDate getDate();
    String getTime();
    String getStatus();
    Long getUserid();
    String getBandName();
}