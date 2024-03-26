package com.example.login.registration;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletResponse;

import com.example.login.appuser.AppUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;

/*
 * This is the Controller for Registration.
 * These have the endpoints used by the frontend
 * to register and login into the sytem. Uses
 * both Registration and AppUser Service.
 */

@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/registration")
@AllArgsConstructor
public class RegistrationController {
    
    private AppUserService appUserService;
    private RegistrationService registrationService;

    /*
     * This post endpoint standard registers users to the web application
     * adding a new appuser and sending a confirmation email.
     */
    @PostMapping
    public JsonNode register(@RequestBody RegistrationRequest request){
        registrationService.register(request);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();
        ((ObjectNode) jNode).put("message", "Confirmation Email Sent"); 
        return jNode;
    }

    /*
     * This get endpoint confirms the email of an
     * app user using a token sent to the email of the user.
     */
    @GetMapping(path = "confirm")
    public void confirm(HttpServletResponse httpServletResponse, @RequestParam("token") String token) {
        registrationService.confirmToken(token);
        httpServletResponse.setHeader("Location", "http://troymarketmusicians.org.s3-website-us-east-1.amazonaws.com/");
        httpServletResponse.setStatus(302);
    }


    /*
     * This post endpoint standard logs in the user. Returns
     * user information if verified. 
     */
    @PostMapping(path="login")
    public JsonNode login(@RequestBody String data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(data);
        String email = root.path("email").asText();
        String pass = root.path("password").asText();

        return appUserService.verifyAccount(email, pass);
    }

    /*
     * This post endpoint logs in or registers the user with Google OAuth.
     * Returns user information if verified. 
     */
    @PostMapping(path="loginGoogle")
    public JsonNode loginGoogle(@RequestBody String data) throws GeneralSecurityException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(data);
        String clientId = root.path("clientId").asText();
        String credential = root.path("credential").asText();

        return appUserService.googleLogin(clientId, credential);
    }

    /*
     * This get endpoint checks if user's current session is expired
     * or not. Returns error if expired or is invalid.
     */
    @GetMapping(path="active")
    public JsonNode isActive(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email) throws GeneralSecurityException, IOException{
        return registrationService.isActive(token, email);
    }

    /*
     * This post endpoint logs out the user, deleting their
     * session from the database.
     */
    @PostMapping(path="logout")
    public JsonNode logout(@RequestHeader(value="Token") String token, @RequestHeader(value="Email") String email) throws GeneralSecurityException, IOException{
        return registrationService.logout(token, email);
    }
}