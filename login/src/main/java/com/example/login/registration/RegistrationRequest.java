package com.example.login.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


/*
 * This RegistrationRequest class which is used for registering
 * new users through the standard method.
 */

@Getter
@AllArgsConstructor

@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    
    private final String contactName;
    private final String email;
    private final String password;

    /*
     * This is an alternative constructor for the RegistrationRequest class.
     * Sets all to null.
     */
    public RegistrationRequest(){
        this.contactName=null;
        this.email=null;
        this.password=null;
    }

}
