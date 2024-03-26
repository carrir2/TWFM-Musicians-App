package com.example.login.appuser;

import java.util.Collection;
import java.util.Collections;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * This AppUser class represents both standard and admin users
 * for the web application. It holds users' important 
 * account information for logging into the system.
 */

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class AppUser implements UserDetails{
    
    @Id
    @SequenceGenerator(
        name = "user_sequence",
        sequenceName = "user_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_sequence"
    )
    private Long id;
    private String contactName;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    @Enumerated(EnumType.STRING)
    private Provider provider;
    private Boolean locked = false;
    private Boolean enabled = false;


    /*
     * This is the constructor for the AppUser class. Initializes contact name, 
     * email, password, user role, and provider.
     */
    public AppUser(String contactName, String email, String password, AppUserRole appUserRole, Provider provider) {
        this.contactName = contactName;
        this.email = email;
        this.password = password;
        this.appUserRole = appUserRole;
        this.provider = provider;
    }
    

    /*
     * Gets the User's role (user, admin)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singletonList(authority);
    }

    /*
     * Returns password of AppUser.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /*
     * Returns username(email) of AppUser
     */
    @Override
    public String getUsername() {
        return email;
    }

    /*
     * Returns account expiration status. Not applicable here.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /*
     * Returns if account is locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    /*
     * Returns credentials expiration status. Not applicable here
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /*
     * Returns if account is enabled. Enabled by email confirmation.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
