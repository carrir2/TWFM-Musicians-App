package com.example.login.musiciandetails;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.login.availabilities.AvailabilitiesService;
import com.example.login.registration.RegistrationService;
import com.example.login.registration.token.ConfirmationToken;
import com.example.login.registration.token.ConfirmationTokenService;
import com.example.login.samples.Samples;
import com.example.login.samples.SamplesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

/*
 * This is the Controller for Musician Details.
 * These have the endpoints used by the frontend
 * to access the musician details services and 
 * other additional services.
 */

@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/registration")
@AllArgsConstructor
public class MusicianDetailsController {
    
    private MusicianDetailsService musicianDetailsService;
    private AvailabilitiesService availabilitiesService;
    private SamplesService samplesService;
    private ConfirmationTokenService confirmationTokenService;
    private RegistrationService registrationService;

    /*
     * This post endpoint uses the musician details,
     * availabilities, and samples service to update
     * information/data associated with a user
     * with a JSON.
     */
    @PostMapping(path="form")
    public void updateDetails(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @RequestBody String data) throws GeneralSecurityException, IOException {
        if (!registrationService.verifyUser(token, email))
            return;
        MusicianDetails detail = musicianDetailsService.updateDetails(data);
        availabilitiesService.updateAvailability(detail.getId(), data);
        samplesService.updateSamples(detail.getId(), data);
    }

    /*
     * This post endpoint returns the information of a user's
     * musician details using their login token and email.
     */
    @PostMapping(path = "/musician")
    public MusicianDetailsObject getMusician(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email) throws JsonProcessingException {
        if (!registrationService.verifyUser(token, email))
            return null;
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
        .orElseThrow(() ->
                new IllegalStateException("token not found"));
        return musicianDetailsService.getMusician(confirmationToken.getAppUser().getId());
    }

    /*
     * This get endpoint returns a queue list of musicians 
     * that are avialable for a specified date.
     */
    @GetMapping(path = "/musicianqueue/{date}")
    public List<MusicianDetailsObject> getMusicianQueue(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @PathVariable("date") String date){
        if (!registrationService.verifyAdmin(token, email))
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return musicianDetailsService.getMusicianQueue(localDate);
    }

    /*
     * This get endpoint returns a queue list of musicians 
     * that are avialable for a specified date and time.
     */
    @GetMapping(path = "/musicianqueue/{date}/{time}")
    public List<MusicianDetailsObject> getMusicianQueue(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @PathVariable("date") String date, @PathVariable("time") String time){
        if (!registrationService.verifyAdmin(token, email))
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return musicianDetailsService.getMusicianQueue(localDate, time);
    }

    /*
     * This get endpoint returns a list of samples 
     * from a user through user id.
     */
    @GetMapping(path = "samples")
    public List<Samples> getSamplesById(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email, @RequestBody String data) throws JsonProcessingException {
        if (!registrationService.verifyUser(token, email))
            return null;        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(data);
        Long userid = Long.parseLong(root.path("userid").asText());
        return samplesService.getSamplesById(userid);
    }


}
