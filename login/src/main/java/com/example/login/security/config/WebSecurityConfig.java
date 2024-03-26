package com.example.login.security.config;

import com.example.login.OAuth2.CustomOAuth2User;
import com.example.login.OAuth2.CustomOAuth2UserService;
import com.example.login.appuser.AppUserService;
import lombok.AllArgsConstructor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    
    private final AppUserService appUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/api/v*/registration/**", "/oauth/**")
                    .permitAll()
                .anyRequest()
                .authenticated().and()
                .formLogin().defaultSuccessUrl("http://localhost:4200", true).and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(oauthUserService).and()
                .successHandler(new AuthenticationSuccessHandler() {
            
                @Override
                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                    CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            
                    appUserService.processOAuthPostLogin(oauthUser.getEmail(), oauthUser.getName());
            
                    response.sendRedirect("http://localhost:4200");
                }
            });
        return http.build();
    }
    
 



    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }

}