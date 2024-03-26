package com.example.login.registration.token;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

/*
 * This is the Service class for ConfirmationToken. This class
 * is used for saving, retrieving, and updating confirmation tokens.
 */

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    
    private final ConfirmationTokenRepository confirmationTokenRepository;

    /*
     * Saves confirmation token to the database.
     */
    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }
    
    /*
     * Retrieves confirmation token from the database.
     */
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    /*
     * Deletes confirmation token from the database.
     */
    public void deleteToken(Long id){
        confirmationTokenRepository.deleteById(id);
    }

    /*
     * Updates confirmation token's confirmation time.
     */
    public int setConfirmationTime(String token) {
        return confirmationTokenRepository.updateConfirmationTime(
                token, LocalDateTime.now());
    }

    public void removeExpirations(){
        System.out.println("IASHDASGHIDUIASDHUISD");
        confirmationTokenRepository.deleteExpirations(LocalDateTime.now());
    }
}
