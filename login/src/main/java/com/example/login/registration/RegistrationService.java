package com.example.login.registration;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.login.appuser.AppUser;
import com.example.login.appuser.AppUserRole;
import com.example.login.appuser.AppUserService;
import com.example.login.appuser.Provider;
import com.example.login.email.EmailSender;
import com.example.login.registration.token.ConfirmationToken;
import com.example.login.registration.token.ConfirmationTokenRepository;
import com.example.login.registration.token.ConfirmationTokenService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;

/*
 * This is the Service class for Registration. This class
 * is used for registering new accounts, logging in and out,
 * confirming accounts, and verifying sessions.
 */

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    /*
     * Standard registration for new users.
     * Will check if email is not in use and is valid.
     * If valid, will send verification email.
     */
    public String register(RegistrationRequest request){
        boolean valid = emailValidator.test(request.getEmail());

        if (!valid){
            throw new IllegalStateException("Invalid Email");
        }
        String token =  appUserService.registerUser(new AppUser(request.getContactName(),
                                                        request.getEmail().toLowerCase(), request.getPassword(), AppUserRole.USER, Provider.LOCAL));
        

        String link = "http://troymarketmusicianservice-env.eba-uzipcwm3.us-east-1.elasticbeanstalk.com/api/v1/registration/confirm?token="+token;
        emailSender.send(request.getEmail(), buildEmail(request.getContactName(), link));
        return "Email Verification Sent";
    }

    /*
     * Confirms a token for a user, enabling the app user account.
     */
    @Transactional
    public String confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));
        if (!confirmationToken.getType().equals("email")) {
            throw new IllegalStateException("invalid token");
        }


        if (confirmationToken.getConfirmationTime() != null) {
            throw new IllegalStateException("email already confirmed");
        }
        

        LocalDateTime expiredAt = confirmationToken.getExpirationTime();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmationTime(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }

    /*
     * Builds the verification email to be sent to the user.
     */
    private String buildEmail(String name, String link) {
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
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Market Musicians Confirmation</span>\n" +
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
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 1 hour. <p>See you soon</p>" +
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

    /*
     * Checks if user's login session is expired or not.
     */
    public JsonNode isActive(String token, String email) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();
        LocalDateTime expiredAt = confirmationToken.getExpirationTime();
        
        if (!confirmationToken.getType().equals("login")) {
            ((ObjectNode) jNode).put("error", "invalid token");
        }else if (expiredAt.isBefore(LocalDateTime.now())) {
            ((ObjectNode) jNode).put("error", "token expired");
            confirmationTokenRepository.delete(confirmationToken);
        } else{
            confirmationToken.setExpirationTime(LocalDateTime.now().plusDays(3));
            ((ObjectNode) jNode).put("error", "none");
        }
        return jNode;
    }

    public boolean verifyUser(String token, String email) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        LocalDateTime expiredAt = confirmationToken.getExpirationTime();
        
        if (!confirmationToken.getType().equals("login") && 
            confirmationToken.getAppUser().getAppUserRole()!=AppUserRole.USER && 
            confirmationToken.getAppUser().getEmail()!=email.toLowerCase()) {
            return false;
        }else if (expiredAt.isBefore(LocalDateTime.now())) {
            confirmationTokenRepository.delete(confirmationToken);
            return false;
        }

        confirmationToken.setExpirationTime(LocalDateTime.now().plusDays(3));
        return true;
    }  

    public boolean verifyAdmin(String token, String email) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        LocalDateTime expiredAt = confirmationToken.getExpirationTime();
        
        if (!confirmationToken.getType().equals("login") && 
            confirmationToken.getAppUser().getAppUserRole()!=AppUserRole.ADMIN && 
            confirmationToken.getAppUser().getEmail()!=email.toLowerCase()) {
            return false;
        }else if (expiredAt.isBefore(LocalDateTime.now())) {
            confirmationTokenRepository.delete(confirmationToken);
            return false;
        }

        confirmationToken.setExpirationTime(LocalDateTime.now().plusDays(3));
        return true;
    }   

    /*
     * Deletes user's login session token from database.
     */
    public JsonNode logout(String token, String email) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
        .orElseThrow(() ->
                new IllegalStateException("token not found"));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();
        
        if (!confirmationToken.getType().equals("login")) {
            ((ObjectNode) jNode).put("error", "invalid token");
        } else{
            confirmationTokenRepository.delete(confirmationToken);
            ((ObjectNode) jNode).put("error", "none");
        }    
        
        return jNode;
    }


}
