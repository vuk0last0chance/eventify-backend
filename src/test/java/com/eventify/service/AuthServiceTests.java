package com.eventify.service;

import com.eventify.config.JwtUtil;
import com.eventify.dto.AuthResponseDTO;
import com.eventify.dto.RegisterRequestDTO;
import com.eventify.entity.Korisnik;
import com.eventify.entity.Uloga;
import com.eventify.exception.EmailAlreadyExistsException;
import com.eventify.repository.KorisnikRepository;
import com.eventify.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTests {

    @Test
    void testRegisterUser_Success() {
        KorisnikRepository repo = mock(KorisnikRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthenticationManager am = mock(AuthenticationManager.class);
        JwtUtil jwt = mock(JwtUtil.class);

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setIme("Ivan");
        dto.setEmail("ivan@test.com");
        dto.setLozinka("StrongP4ss");

        when(repo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(encoder.encode(dto.getLozinka())).thenReturn("hash");
        when(repo.save(Mockito.any())).thenAnswer(inv -> {
            Korisnik k = inv.getArgument(0);
            k.setId(1L);
            return k;
        });
        when(jwt.generateToken(anyLong(), anyString(), any())).thenReturn("jwt-token");

        AuthService service = new AuthServiceImpl(repo, encoder, am, jwt);
        AuthResponseDTO res = service.registerUser(dto);

        assertNotNull(res);
        assertEquals("jwt-token", res.getToken());
        assertEquals("Ivan", res.getUser().getIme());
        verify(repo, times(1)).save(any());
    }

    @Test
    void testRegisterUser_ThrowsException_WhenEmailExists() {
        KorisnikRepository repo = mock(KorisnikRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthenticationManager am = mock(AuthenticationManager.class);
        JwtUtil jwt = mock(JwtUtil.class);

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setIme("Ana");
        dto.setEmail("ana@test.com");
        dto.setLozinka("StrongP4ss");

        when(repo.findByEmail(dto.getEmail())).thenReturn(Optional.of(
                Korisnik.builder().id(1L).email(dto.getEmail()).uloga(Uloga.KORISNIK).build()
        ));

        AuthService service = new AuthServiceImpl(repo, encoder, am, jwt);
        assertThrows(EmailAlreadyExistsException.class, () -> service.registerUser(dto));
    }
}