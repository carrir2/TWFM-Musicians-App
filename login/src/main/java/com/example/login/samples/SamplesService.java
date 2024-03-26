package com.example.login.samples;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

/*
 * This is the Service class for Samples. This class
 * stores a user's samples into the database using the
 * samples repository.
 */

@Service
@AllArgsConstructor
public class SamplesService {
    
    private final SamplesRepository samplesRepository;

    /*
     * Updates the samples of a user into the database.
     */ 
    public void updateSamples(Long userid, String data) throws GeneralSecurityException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(data);
        JsonNode samples = root.get("externalLinks");

        String [] links =  new String[samples.size()];
        int count=0;
        String tempLink;
        for (JsonNode objNode : samples){
            tempLink = objNode.toString();
            tempLink = tempLink.replace("\"","");
            links[count]= tempLink;
            System.out.println(links[count]);
            count++;
        }
        

        samplesRepository.deleteByUserid(userid);
    
        for (int i=0; i < links.length; i++){
            Samples temp = new Samples(userid, links[i]);
            samplesRepository.save(temp);
        }
    }

    /*
     * Gets a list of samples by a user from the database.
     */ 
    public List<Samples> getSamplesById(Long userid){
        return samplesRepository.getByUserid(userid);
    }
}
