package com.eventify.config;

import com.eventify.repository.KorisnikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Omogućava @PreAuthorize, @PostAuthorize, itd.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final KorisnikRepository korisnikRepository;

    /**
     * Glavni filter lanac koji definiše pravila bezbednosti za HTTP zahteve.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Onemogućavamo CSRF (Cross-Site Request Forgery) jer koristimo JWT (stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Definišemo pravila autorizacije za različite putanje
                .authorizeHttpRequests(auth -> auth
                        // Endpoints za registraciju i prijavu su javno dostupni svima
                        .requestMatchers("/api/auth/**").permitAll()
                        // Javni pregled događaja je dozvoljen (GET zahtevi)
                        .requestMatchers(HttpMethod.GET, "/api/dogadjaji/**").permitAll()
                        // Svi ostali zahtevi zahtevaju autentifikaciju
                        .anyRequest().authenticated()
                )

                // Konfigurišemo upravljanje sesijama da bude stateless, jer JWT to omogućava
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Dodajemo naš custom JWT filter pre standardnog filtera za username/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Definiše kako Spring Security treba da učitava podatke o korisniku.
     * U našem slučaju, učitava ih iz baze preko KorisnikRepository.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> korisnikRepository.findByEmail(username)
                .map(k -> User.withUsername(k.getEmail())
                        .password(k.getLozinkaHash())
                        .roles(k.getUloga().name()) // Spring automatski dodaje "ROLE_" prefiks
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen: " + username));
    }

    /**
     * Definiše provajdera za autentifikaciju koji koristi UserDetailsService i PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Izlaže AuthenticationManager kao Bean, neophodan za ručnu autentifikaciju u AuthService.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Definiše koji algoritam za heširanje lozinki koristimo.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}