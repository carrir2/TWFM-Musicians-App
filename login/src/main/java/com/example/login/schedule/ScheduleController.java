package com.example.login.schedule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.login.registration.RegistrationService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;

/*
 * This is the Controller for Schedules.
 * These have the endpoints used by the frontend
 * to access the schedule services.
 */

@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/registration/schedule")
@AllArgsConstructor
public class ScheduleController {
    private ScheduleService scheduleService;
    private RegistrationService registrationService;

    /*
     * This get endpoint gets list of all schedule musician data.
     */
    @GetMapping
    public List<ScheduleMusicianData> getScheduleAll(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email){
        if (!registrationService.verifyAdmin(token, email))
            return null;             
        return scheduleService.getScheduleAll();
    }

    /*
     * This get endpoint gets list of all schedule data for a specified
     * date.
     */
    @GetMapping(path = "{date}")
    public List<ScheduleMusicianData> getAllScheduleByDate(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @PathVariable("date") String date){
        if (!registrationService.verifyAdmin(token, email))
            return null;     
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return scheduleService.getAllScheduleByDate(localDate);
    }


    /*
     * This post endpoint refreshes the scheduling data for the indoor season
     * of a specified year.
     */
    @PostMapping(path = {"/refreshindoor/{year}", "/refreshIndoor/{year}"})
    public void refreshIndoor(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @PathVariable("year") String year){
        if (!registrationService.verifyAdmin(token, email))
            return;             
        int yearI = Integer.parseInt(year);

        scheduleService.setupIndoor(yearI);
    }

    /*
     * This post endpoint refreshes the scheduling data for the outdoor season
     * of a specified year.
     */
    @PostMapping(path = {"/refreshoutdoor/{year}", "/refreshIndoor/{year}"})
    public void refreshOutdoor(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @PathVariable("year") String year){
        if (!registrationService.verifyAdmin(token, email))
            return;     
        int yearI = Integer.parseInt(year);

        scheduleService.setupIndoor(yearI);
    }

    /*
     * This post endpoint is for admins to select a musician for a 
     * specified schedule slot.
     */
    @PostMapping(path="/select/{slotid}/{userid}")
    public JsonNode selectMusician(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @PathVariable("slotid") Long slotid, @PathVariable("userid") Long userid){
        if (!registrationService.verifyAdmin(token, email))
            return null;     
        return scheduleService.selectMusician(slotid, userid);
    }

    /*
     * This post endpoint is for admins to removes a musician for a 
     * specified schedule slot.
     */
    @PostMapping(path="/unselect/{slotid}/{userid}")
    public JsonNode unselect(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @PathVariable("slotid") Long slotid, @PathVariable("userid") Long userid){
        return scheduleService.unselectMusician(slotid, userid);
    }

    /*
     * This post endpoint is for admins to send an invitation to a musician for a 
     * specified schedule slot.
     */
    @PostMapping(path="/invite/{slotid}/{userid}")
    public JsonNode inviteMusician(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @PathVariable("slotid") Long slotid, @PathVariable("userid") Long userid){
        if (!registrationService.verifyAdmin(token, email))
            return null;      
        return scheduleService.sendInvite(slotid, userid);
    }

    /*
     * This get endpoint is used by musicians to respond to invites
     * by accepting or declining.
     */
    @GetMapping(path="/invitation/{token}")
    public void inviteMusician(HttpServletResponse httpServletResponse, @PathVariable("token") String token, @RequestParam("choice") String choice){
        scheduleService.respondInvite(token, choice);
        httpServletResponse.setHeader("Location", "http://troymarketmusicians.org.s3-website-us-east-1.amazonaws.com/");
        httpServletResponse.setStatus(302);
    }

}
