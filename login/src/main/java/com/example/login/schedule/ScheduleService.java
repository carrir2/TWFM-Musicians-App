package com.example.login.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.login.appuser.AppUser;
import com.example.login.appuser.UserRepository;
import com.example.login.availabilities.Availabilities;
import com.example.login.availabilities.AvailabilitiesRepository;
import com.example.login.email.EmailSender;
import com.example.login.musiciandetails.MusicianDetails;
import com.example.login.musiciandetails.MusicianDetailsRepository;
import com.example.login.registration.token.ConfirmationToken;
import com.example.login.registration.token.ConfirmationTokenService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;

/*
 * This is the Service class for Schedule. This class
 * stores and updates information about a scheduling 
 * into the database. It also retrieves data using
 * other repositories.
 */

@Service
@AllArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MusicianDetailsRepository musicianDetailsRepository;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final AvailabilitiesRepository availabilitiesRepository;

    /*
     * This function gets a list of all ScheduleMusicianData 
     * from the database.
     */
    public List<ScheduleMusicianData> getScheduleAll() {
        return scheduleRepository.findAllSchedule();
    }

     /*
     * This function gets a list of all ScheduleMusicianData 
     * from the database by a specified date.
     */   
    public List<ScheduleMusicianData> getAllScheduleByDate(LocalDate date){
        return scheduleRepository.findAllByDate(date);
    }

    /*
     * This function clears and sets up the schedule slots
     * for the indoor season by a specified year.
     */   
    public void setupIndoor(int year){
        LocalDate dateStart = LocalDate.of(year, 11, 1);
        if (dateStart.isBefore(LocalDate.now())){
            dateStart = LocalDate.now();
        }
        LocalDate dateEnd = LocalDate.of(year+1, 4, 30);
        scheduleRepository.deleteBySeason(dateStart, dateEnd);
        List<ScheduleSlot> schedules = new ArrayList<ScheduleSlot>();
        String[] times = {"AM", "PM"};

        for (; dateStart.isBefore(dateEnd); dateStart = dateStart.plusDays(1)){
            if (DayOfWeek.of(dateStart.get(ChronoField.DAY_OF_WEEK))==DayOfWeek.SATURDAY){
                for (int j = 0; j < times.length; j++){
                    ScheduleSlot schedTemp = new ScheduleSlot(dateStart, "Stage A", times[j]);
                    schedules.add(schedTemp);
                }
            }
        }
        scheduleRepository.saveAll(schedules);
    }

    /*
     * This function clears and sets up the schedule slots
     * for the outdoor season by a specified year.
     */   
    public void setupOutdoor(int year){
        LocalDate dateStart = LocalDate.of(year, 5, 1);
        if (dateStart.isBefore(LocalDate.now())){
            dateStart = LocalDate.now();
        }
        LocalDate dateEnd = LocalDate.of(year, 10, 31);
        scheduleRepository.deleteBySeason(dateStart, dateEnd);
        List<ScheduleSlot> schedules = new ArrayList<ScheduleSlot>();
        String[] times = {"AM", "PM"};
        String[] stages = {"Stage A", "Stage B", "Stage C"};
        for (; dateStart.isBefore(dateEnd); dateStart = dateStart.plusDays(1)){
            if (DayOfWeek.of(dateStart.get(ChronoField.DAY_OF_WEEK))==DayOfWeek.SATURDAY){
                for (int j = 0; j < times.length; j++){
                    for (int k = 0; k < stages.length; k++){
                        ScheduleSlot schedTemp = new ScheduleSlot(dateStart, stages[k], times[j]);
                        schedules.add(schedTemp);
                    }

                }
            }
        }
        scheduleRepository.saveAll(schedules);
    }

    /*
     * This function is for admins to select a musician
     * for a specified schedule slot.
     */   
    public JsonNode selectMusician(Long slotid, Long userid){

        ScheduleSlot slot =  scheduleRepository.findSlotBySlotID(slotid);
        AppUser appUser = userRepository.findUserById(userid);
        Availabilities available = availabilitiesRepository.findByDateAndId(slot.getDate(), userid);
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();

        if (slot==null || appUser==null || available==null){
            ((ObjectNode) jNode).put("error", "invalid request");
            return jNode;
        }

        if (!slot.getStatus().equals("unfilled")){ 
            Availabilities availbleOld = availabilitiesRepository.findByDateAndId(slot.getDate(), slot.getUserid());
            if (availbleOld!=null){
                if (slot.getStatus().equals("declined")){
                    if (slot.getTime().equals("AM")){
                        availbleOld.setStatusAM("declined");
                    } else{
                        availbleOld.setStatusPM("declined");
                    }
                } else {
                    if (slot.getTime().equals("AM")){
                        availbleOld.setStatusAM("none");
                    } else{
                        availbleOld.setStatusPM("none");
                    }
                }
                if (slot.getStatus().equals("accepted")){
                    MusicianDetails details = musicianDetailsRepository.getMusicianById(slot.getUserid());
                    details.setTotalPerformances(details.getTotalPerformances()-1);
                }
            }
        } 

        slot.setUserid(userid);
        slot.setStatus("no invite");
        if (slot.getTime().equals("AM")){
            available.setStatusAM("selected");
        } else{
            available.setStatusPM("selected");
        }
        scheduleRepository.save(slot);

        ((ObjectNode) jNode).put("error", "none");
        return jNode;
    }

    /*
     * This function is for admins to clear a musician
     * for a specified schedule slot.
     */   
    public JsonNode unselectMusician(Long slotid, Long userid){
        ScheduleSlot slot =  scheduleRepository.findSlotBySlotID(slotid);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();

        if (slot==null || slot.getUserid()!=userid){
            ((ObjectNode) jNode).put("error", "invalid request");
            return jNode;
        }

        if (!slot.getStatus().equals("unfilled")){ 
            Availabilities availbleOld = availabilitiesRepository.findByDateAndId(slot.getDate(), slot.getUserid());
            if (availbleOld!=null){
                if (slot.getStatus().equals("declined")){
                    if (slot.getTime().equals("AM")){
                        availbleOld.setStatusAM("declined");
                    } else{
                        availbleOld.setStatusPM("declined");
                    }
                } else {
                    if (slot.getTime().equals("AM")){
                        availbleOld.setStatusAM("none");
                    } else{
                        availbleOld.setStatusPM("none");
                    }
                }
                if (slot.getStatus().equals("accepted")){
                    MusicianDetails details = musicianDetailsRepository.getMusicianById(slot.getUserid());
                    details.setTotalPerformances(details.getTotalPerformances()-1);
                }
            }
        } 

        slot.setUserid(null);
        slot.setStatus("unfilled");
        scheduleRepository.save(slot);
        ((ObjectNode) jNode).put("error", "none");
        return jNode;
    }

    /*
     * This function is for admins to invite a musician
     * for a specified schedule slot.
     */   
    public JsonNode sendInvite(Long slotid, Long userid){
        ScheduleSlot slot =  scheduleRepository.findSlotBySlotID(slotid);
        AppUser appUser = userRepository.findUserById(userid);
        Availabilities available = availabilitiesRepository.findByDateAndId(slot.getDate(), userid);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();

        if (slot==null || appUser==null || available==null || slot.getUserid()!=userid){
            ((ObjectNode) jNode).put("error", "invalid request");
            return jNode;
        }

        slot.setStatus("pending");
        

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), "invite", appUser, slot);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://troymarketmusicianservice-env.eba-uzipcwm3.us-east-1.elasticbeanstalk.com/api/v1/registration/schedule/invitation/"+token+"?choice=";
        emailSender.send(appUser.getEmail(), buildEmail(appUser.getContactName(), link, slot), "Troy Waterfront Farmers Market Invitation");
        scheduleRepository.save(slot);
        ((ObjectNode) jNode).put("error", "none");

        return jNode;
    }

    /*
     * This function is for invited musicians to respond
     * to an invite for a specified schedule slot.
     */   
    public JsonNode respondInvite(String token, String choice){
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();

        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
        .orElseThrow(() ->
                new IllegalStateException("token not found"));

        if (!confirmationToken.getType().equals("invite")) {
            ((ObjectNode) jNode).put("error", "invalid token");
            return jNode;
        }
        if (confirmationToken.getConfirmationTime() != null || !confirmationToken.getSlot().getStatus().equals("pending")) {
            ((ObjectNode) jNode).put("error", "response already received");
            return jNode;
        }

        LocalDateTime expiredAt = confirmationToken.getExpirationTime();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            ((ObjectNode) jNode).put("error", "invitation expired");
            return jNode;
        }

        ScheduleSlot slot = confirmationToken.getSlot();

        if (choice.equals("accept")){
            slot.setStatus("accepted");
            choice = "Invite Accepted";
            MusicianDetails details = musicianDetailsRepository.getMusicianById(confirmationToken.getAppUser().getId());
            details.setTotalPerformances(details.getTotalPerformances()+1);

        } else if (choice.equals("decline")){
            slot.setStatus("declined");
            choice = "Invite Declined";
        } else{
            ((ObjectNode) jNode).put("error", "invalid choice");
            return jNode;
        }

        confirmationTokenService.setConfirmationTime(token);
        ((ObjectNode) jNode).put("error", choice);
        return jNode;

    }

    /*
     * This function is to build the email that will be sent
     * to an invited musician.
     */
    private String buildEmail(String name, String link, ScheduleSlot slot) {
        String stage = slot.getStage();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String date = slot.getDate().format(formatter);
        String time;
        if (slot.getTime().equals("AM")){
            time = "9:00AM - 11:00AM";
        } else{
            time = "11:30AM - 2:00PM";
        }

        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Market Musicians Invitation</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#A5534D\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> You have been invited to perform at the Troy Waterfront Farmers Market. </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 5px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> "+ date + "</p><p style=\"Margin:0 0 5px 0;font-size:19px;line-height:25px;color:#0b0c0c\">"+ time+ "</p><p style=\"Margin:0 0 15px 0;font-size:19px;line-height:25px;color:#0b0c0c\">"+stage+" </p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "accept"+"\">Accept</a></p><p><a href=\"" + link + "decline"+"\">Decline</a></p></blockquote>Please respond as soon as possible.<p>Email <a href=\"mailto:erin.cook@troymarket.org\">erin.cook@troymarket.org</a> for any questions.</p>\n<p>Hope to see you there!</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}