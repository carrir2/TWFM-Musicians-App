package com.example.login.registration;

import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.example.login.appuser.UserRepository;

import lombok.AllArgsConstructor;

/*
 * This class checks the database for a user's email.
 */

@AllArgsConstructor
@Service
public class EmailValidator implements Predicate<String>{
    
    private final UserRepository userRepository;

    /*
     * This function checks if email can be found in the AppUser database.
     */
    @Override
    public boolean test(String s) {

        return !userRepository.findByEmail(s).isPresent();
    }

}
