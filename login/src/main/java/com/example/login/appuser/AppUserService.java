package com.example.login.appuser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import com.example.login.musiciandetails.MusicianDetails;
import com.example.login.musiciandetails.MusicianDetailsRepository;
import com.example.login.musiciandetails.MusicianDetailsService;
import com.example.login.registration.token.ConfirmationToken;
import com.example.login.registration.token.ConfirmationTokenService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;


/*
 * This is the Service class for AppUsers. This class
 * uses the user repository database for registering, 
 * logging in, and enabling accounts. The service
 * also uses other services and repositories to function.
 */
@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService{
    
    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    private final UserRepository userRepository;
    private final MusicianDetailsService musicianDetailsService;
    private final MusicianDetailsRepository musicianDetailsRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    
    /*
     * Gets users details object by username(email).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG)));
    }

    /*
     * Registers new user through standard method.
     */
    public String registerUser(AppUser appUser){
        boolean exist = userRepository.findByEmail(appUser.getEmail()).isPresent();
        AppUser userTemp;
        MusicianDetails detailTemp;

        if (exist){
            userTemp = userRepository.findUserByEmail(appUser.getEmail());
            
            if (userTemp.getEnabled()){
                throw new IllegalStateException("Email taken");
            } else{
                detailTemp = musicianDetailsRepository.getMusicianById(userTemp.getId());
                confirmationTokenService.deleteToken(userTemp.getId());
                userRepository.delete(userTemp);
                musicianDetailsRepository.delete(detailTemp);
                
            }

            
        }
        detailTemp = new MusicianDetails(appUser.getEmail(), appUser.getContactName());
        detailTemp.setId(appUser.getId());
        String encoded = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encoded);
        userRepository.save(appUser);
        detailTemp.setId(appUser.getId());
        musicianDetailsRepository.save(detailTemp);


        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), "email", appUser);

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    /*
     * Changes enable status of user to true after email confirmation.
     */
    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }

    /*
     * Checks if OAuth user is in system. 
     * If not, it will create a new user.
     * Otherwise will change enabled status of user.
     */
    public void processOAuthPostLogin(String email, String name) {
        AppUser tempUser = userRepository.findUserByEmail(email);
        if (tempUser == null){
            AppUser newUser = new AppUser();
            newUser.setAppUserRole(AppUserRole.USER);
            newUser.setContactName(name);
            newUser.setEmail(email);
            newUser.setProvider(Provider.GOOGLE);
            newUser.setEnabled(true);
            userRepository.save(newUser);
            MusicianDetails detailTemp = new MusicianDetails(email, name);
            detailTemp.setContactName(name);
            detailTemp.setId(newUser.getId());
            musicianDetailsRepository.save(detailTemp);
        } else{
            tempUser.setEnabled(true);
            userRepository.save(tempUser);
        }
    }

    /*
     * Used for logging in, will check database for account.
     * If found, will check password with encrypted and return AppUser details
     * if matched.
     * Otherwise will return invalid credentials.
     */
    public JsonNode verifyAccount(String email, String pass) {
        email = email.toLowerCase();
        AppUser user = userRepository.findUserByEmail(email);


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();
        
        if (!user.getEnabled()){
            ((ObjectNode) jNode).put("error", "Email not confirmed");   
            return jNode; 
        }

        if (bCryptPasswordEncoder.matches(pass, user.getPassword())){

            ((ObjectNode) jNode).put("error", "none");
            ((ObjectNode) jNode).put("id", user.getId().toString());
            ((ObjectNode) jNode).put("email", user.getEmail());
            ((ObjectNode) jNode).put("contactName", user.getContactName().toString());

            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusDays(3), "login", user);
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            ((ObjectNode) jNode).put("token", token);


            ((ObjectNode) jNode).put("appUserRole", user.getAppUserRole().toString());
            ((ObjectNode) jNode).put("locked", user.getLocked());
            ((ObjectNode) jNode).put("enabled", user.getEnabled().toString());
            if (user.getAppUserRole()==AppUserRole.ADMIN){
                musicianDetailsService.refreshData();
                confirmationTokenService.removeExpirations();
            }
            
        } else{
            ((ObjectNode) jNode).put("error", "Invalid Credentials");    
        }

        return jNode;
    }

    /*
     * Used for the Google Oauth Login. Will verify Google account id with Google's verifier. 
     * Next, it will check if account is already in the database.
     * If not, will create new user with Google Account.
     * Afterwards, will return AppUser details.
     */
    public JsonNode googleLogin(String clientId, String credential) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList("755256089864-hf89vbc6uee31ig7rhk3j0bmlpahbu60.apps.googleusercontent.com"))
            .build();
        GoogleIdToken idTokenGoogle = verifier.verify(credential);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.createObjectNode();
        if (idTokenGoogle != null) {
            Payload payload = idTokenGoogle.getPayload();
        

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            processOAuthPostLogin(email, name);
            AppUser user = userRepository.findUserByEmail(email);
            ((ObjectNode) jNode).put("error", "none");
            ((ObjectNode) jNode).put("id", user.getId().toString());
            ((ObjectNode) jNode).put("email", user.getEmail());
            ((ObjectNode) jNode).put("contactName", user.getContactName().toString());
            ((ObjectNode) jNode).put("appUserRole", user.getAppUserRole().toString());
            ((ObjectNode) jNode).put("locked", user.getLocked());
            ((ObjectNode) jNode).put("enabled", user.getEnabled().toString());

            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusDays(3), "login", user);
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            ((ObjectNode) jNode).put("token", token);


        } else {
            ((ObjectNode) jNode).put("error", "Invalid Credentials");    
        }
        return jNode;
    }
}
