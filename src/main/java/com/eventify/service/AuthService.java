package com.eventify.service;

import com.eventify.dto.AuthResponseDTO;
import com.eventify.dto.LoginRequestDTO;
import com.eventify.dto.RegisterRequestDTO;

public interface AuthService {
    /**
     * Registers a new user with role KORISNIK.
     * @param dto registration request
     * @return JWT token + basic user info
     */
    AuthResponseDTO registerUser(RegisterRequestDTO dto);

    /**
     * Authenticates a user and returns a JWT token.
     * @param dto login request
     * @return JWT token + basic user info
     */
    AuthResponseDTO loginUser(LoginRequestDTO dto);
}