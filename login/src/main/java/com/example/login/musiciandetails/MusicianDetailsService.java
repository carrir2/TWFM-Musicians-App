package com.example.login.musiciandetails;
import com.example.login.appuser.AppUser;
import com.example.login.appuser.UserRepository;
import com.example.login.availabilities.Availabilities;
import com.example.login.availabilities.AvailabilitiesRepository;

import com.example.login.samples.Samples;
import com.example.login.samples.SamplesRepository;
import com.example.login.schedule.ScheduleRepository;
import com.example.login.schedule.ScheduleSlot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/*
 * This is the Service class for MusicianDetails. This class
 * stores a user's details from their submitted musician application form
 * into the database. It also retrieves data using other
 * repositories.
 */

@Service
@AllArgsConstructor
public class MusicianDetailsService {
    private final UserRepository userRepository;
    private final MusicianDetailsRepository musicianDetailsRepository;
    private final SamplesRepository samplesRepository;
    private final AvailabilitiesRepository availabilitiesRepository;
    private final ScheduleRepository scheduleRepository;

    /*
     * This function updates the MusicianDetails object for a user,
     * using the data from the musician application form.
     */
    public MusicianDetails updateDetails(String data) throws GeneralSecurityException, IOException{

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(data);
        
        String email = root.path("email").asText();
        String contactName = root.path("contactName").asText();
        String bandName = root.path("bandName").asText();
        String setup = root.path("setup").asText();
        String lastMinute = root.path("lastMinute").asText();
        String phoneNumber = root.path("phoneNumber").asText();
        String paymentHandle = root.path("paymentHandle").asText();
        String gigLength = root.path("gigLength").asText();
        String gigTime = root.path("gigTime").asText();
        String comments = root.path("comments").asText();
        JsonNode availabilities = root.get("availabilities");
        JsonNode samples = root.get("externalLinks");
             
        String [] availArr =  new String[availabilities.size()];
        int count=0;
        for (JsonNode objNode : availabilities){
            availArr[count]= objNode.toString();
            count++;
        }

        String [] samplesArr =  new String[samples.size()];
        count=0;
        for (JsonNode objNode : samples){
            samplesArr[count]= objNode.toString();
            count++;
        }
        
        System.out.println(availabilities.size());
        System.out.println(comments);
        AppUser appUser = userRepository.findUserByEmail(email);
        appUser.setContactName(contactName);
        
        MusicianDetails details = musicianDetailsRepository.getMusicianByEmail(email);
        if (details==null){
            System.out.println("INVALID");
            return null;
        }
        
        details.updateMusicianDetails(email, contactName, bandName, setup, lastMinute, phoneNumber, paymentHandle, gigLength, gigTime, comments); 
        
        details.setTotalAvailability(availabilities.size());
        details.setDateTime(LocalDateTime.now());
        userRepository.save(appUser);
        musicianDetailsRepository.save(details);

        return details;

    }

    /*
     * Gets the MusicianDetails of a user through User ID.
     */
    public MusicianDetailsObject getMusician(Long id){

        MusicianDetails musician = musicianDetailsRepository.getMusicianById(id);
        List<Samples> tempSampleList = samplesRepository.getByUserid(musician.getId());
        String [] tempSampleArr =  new String[tempSampleList.size()];
        for (int j = 0; j < tempSampleList.size(); j++){
            tempSampleArr[j]= tempSampleList.get(j).getLink();
        }
        List<Availabilities> tempAvailList = availabilitiesRepository.getAllAvailByUserid(musician.getId(), LocalDate.now());
        String [] tempAvailArr =  new String[tempAvailList.size()];
        for (int j = 0; j < tempAvailList.size(); j++){
            tempAvailArr[j]= tempAvailList.get(j).getDate().toString();
        }
        musician.setTotalAvailability(tempAvailList.size());

        List<ScheduleSlot> tempSelectList = scheduleRepository.findAllById(id);
        String [] tempSelectArr =  new String[tempSelectList.size()];
        for (int j = 0; j < tempSelectList.size(); j++){
            tempSelectArr[j]= tempSelectList.get(j).getDate().toString();
        }


        MusicianDetailsObject detObj = new MusicianDetailsObject(musician, tempSampleArr, tempAvailArr, tempSelectArr);
        return detObj;

    }

    /*
     * Gets a sorted queue of musicians and their details for a specified date.
     */ 
    public List<MusicianDetailsObject> getMusicianQueue(LocalDate date){ 
        List<MusicianDetails> list = musicianDetailsRepository.getMusicianQueue(date);
        return setData(list);
    }

    /*
     * Gets a sorted queue of musicians and their details for a specified date and time.
     */ 
    public List<MusicianDetailsObject> getMusicianQueue(LocalDate date, String time){
        List<MusicianDetails> list;
        if (time.equals("morning"))
            list = musicianDetailsRepository.getMusicianQueueAM(date, time);
        else 
            list = musicianDetailsRepository.getMusicianQueuePM(date, time);
        return setData(list);
    }

    /*
     * Gets all the samples and availabilities and sets them to a musician detail object.
     * Returns a list of those objects.
     */ 
    private List<MusicianDetailsObject> setData(List<MusicianDetails> list){
        List<MusicianDetailsObject> objList = new ArrayList<MusicianDetailsObject>();

        for (int i = 0; i < list.size(); i++){
            Long idTemp = list.get(i).getId();
            List<Samples> tempSampleList = samplesRepository.getByUserid(idTemp);
            String [] tempSampleArr =  new String[tempSampleList.size()];
            for (int j = 0; j < tempSampleList.size(); j++){
                tempSampleArr[j]= tempSampleList.get(j).getLink();
            }
            List<Availabilities> tempAvailList = availabilitiesRepository.getAllAvailByUserid(idTemp, LocalDate.now());
            String [] tempAvailArr =  new String[tempAvailList.size()];
            for (int j = 0; j < tempAvailList.size(); j++){
                tempAvailArr[j]= tempAvailList.get(j).getDate().toString();
            }
            list.get(i).setTotalAvailability(tempAvailList.size());
      

            List<ScheduleSlot> tempSelectList = scheduleRepository.findAllById(idTemp);
            String [] tempSelectArr =  new String[tempSelectList.size()];
            for (int j = 0; j < tempSelectList.size(); j++){
                tempSelectArr[j]= tempSelectList.get(j).getDate().toString();
            }
            list.get(i).setTotalSelections(tempSelectList.size());

            objList.add(new MusicianDetailsObject(list.get(i), tempSampleArr, tempAvailArr, tempSelectArr));
        }
        return objList;
    }

    /*
     * Updates all total availability values of all users by checking availabilities.
     */
    public void refreshData(){
        List<MusicianDetails> list = musicianDetailsRepository.getAllMusicians();
        for (int i = 0; i < list.size(); i++){
            Long idTemp = list.get(i).getId();
            List<Availabilities> tempAvailList = availabilitiesRepository.getAllAvailByUserid(idTemp, LocalDate.now());
            list.get(i).setTotalAvailability(tempAvailList.size());
        }
    }
}
