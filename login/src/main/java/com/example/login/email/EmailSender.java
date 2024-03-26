package com.example.login.email;

/*
 * Interface for EmailSender
 */
public interface EmailSender {

    void send(String to, String email);

    void send(String to, String email, String subject);
    
}
