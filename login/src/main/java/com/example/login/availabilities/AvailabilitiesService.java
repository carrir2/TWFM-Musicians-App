package com.example.login.availabilities;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

/*
 * This is the Service class for Availabilities. This class
 * stores a user's availabilities into the database using the
 * availabilities repository.
 */

@Service
@AllArgsConstructor
public class AvailabilitiesService {
    
    private final AvailabilitiesRepository availabilitiesRepository;

    /*
     * Updates the availabilities of a user into the database.
     */
    public void updateAvailability(Long userid, String data) throws GeneralSecurityException, IOException{
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(data);
        JsonNode availabilities = root.get("availabilities");

        String [] dates =  new String[availabilities.size()];
        int count=0;
        for (JsonNode objNode : availabilities){
            dates[count]= objNode.toString();
            count++;
        }


        availabilitiesRepository.deleteByUserid(userid);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("\"M/d/yyyy\"");

        for (int i=0; i < dates.length; i++){
            
            LocalDate localDate = LocalDate.parse(dates[i], formatter);
            Availabilities tempAvailabilities = availabilitiesRepository.findByDateAndId(localDate, userid);
            if (tempAvailabilities ==null){
                Availabilities temp = new Availabilities(userid, localDate);
                availabilitiesRepository.save(temp);
            }

        }

    }


}
