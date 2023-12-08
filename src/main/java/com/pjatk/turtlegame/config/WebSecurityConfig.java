package com.pjatk.turtlegame.config;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/error").permitAll();
                    auth.requestMatchers("/logout").permitAll();
                    auth.requestMatchers("/api/turtles/**").permitAll();
                    auth.requestMatchers("/user/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/private-message/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/main/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/expeditions/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/assets/**").permitAll();
                    auth.requestMatchers("/turtles/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/nest/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/items/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/achievements").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/market/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/academy/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/friends/**").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/registration/**").permitAll();
                    auth.requestMatchers("/news").hasAnyAuthority("ADMIN", "USER");
                    auth.requestMatchers("/news/create").hasAuthority("ADMIN");
                    auth.requestMatchers("/news/edit").hasAuthority("ADMIN");
                    auth.requestMatchers("/media/**").permitAll();
                    auth.requestMatchers("/remind-password").permitAll();
                    auth.requestMatchers("/change-password").permitAll();
                    auth.requestMatchers("/guards/**").hasAnyAuthority("ADMIN", "USER");
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/main", true)
                        .permitAll())
                .httpBasic(withDefaults())
                .build();
    }

}