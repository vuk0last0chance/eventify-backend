package com.eventify.service.impl;

import com.eventify.config.JwtUtil;
import com.eventify.dto.AuthResponseDTO;
import com.eventify.dto.LoginRequestDTO;
import com.eventify.dto.RegisterRequestDTO;
import com.eventify.dto.UserInfoDTO;
import com.eventify.entity.Korisnik;
import com.eventify.entity.Uloga;
import com.eventify.exception.EmailAlreadyExistsException;
import com.eventify.repository.KorisnikRepository;
import com.eventify.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponseDTO registerUser(RegisterRequestDTO dto) {
        korisnikRepository.findByEmail(dto.getEmail())
                .ifPresent(k -> { throw new EmailAlreadyExistsException("Email je već registrovan."); });

        Korisnik k = Korisnik.builder()
                .ime(dto.getIme())
                .email(dto.getEmail())
                .lozinkaHash(passwordEncoder.encode(dto.getLozinka()))
                .uloga(Uloga.KORISNIK)
                .build();

        k = korisnikRepository.save(k);

        String token = jwtUtil.generateToken(k.getId(), k.getEmail(), k.getUloga());
        return new AuthResponseDTO(token, new UserInfoDTO(k.getId(), k.getIme(), k.getEmail(), k.getUloga()));
    }

    @Override
    public AuthResponseDTO loginUser(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getLozinka())
        );
        Korisnik k = korisnikRepository.findByEmail(dto.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(k.getId(), k.getEmail(), k.getUloga());
        return new AuthResponseDTO(token, new UserInfoDTO(k.getId(), k.getIme(), k.getEmail(), k.getUloga()));
    }
}