package com.eventify.controller;

import com.eventify.dto.PorudzbinaRequestDTO;
import com.eventify.entity.Porudzbina;
import com.eventify.service.PorudzbinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PorudzbinaController {

    private final PorudzbinaService porudzbinaService;

    @PostMapping("/api/porudzbine") // Putanja za kreiranje porudžbine
    @PreAuthorize("hasRole('KORISNIK')")
    public ResponseEntity<Porudzbina> create(@Valid @RequestBody PorudzbinaRequestDTO dto, Authentication auth) {
        Long korisnikId = getUserIdFromAuth(auth);
        return ResponseEntity.ok(porudzbinaService.createPorudzbina(dto, korisnikId));
    }

    @GetMapping("/api/users/me/orders") // Ispravljena putanja
    @PreAuthorize("hasAnyRole('KORISNIK', 'ORGANIZATOR', 'ADMIN')")
    public ResponseEntity<List<Porudzbina>> myOrders(Authentication auth) {
        Long korisnikId = getUserIdFromAuth(auth);
        return ResponseEntity.ok(porudzbinaService.getPorudzbineByKorisnik(korisnikId));
    }

    /**
     * Siguran način da se izvuče ID korisnika iz Authentication objekta.
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("Authentication object is invalid.");
        }

        // Proveravamo da li je 'details' objekat tipa Long, što smo postavili u filteru.
        if (authentication.getDetails() instanceof Long) {
            return (Long) authentication.getDetails();
        } else {
            // Ako nije, pokušavamo da izvučemo email iz Principal-a i nađemo korisnika
            // (Ovo je fallback, ali ne bi trebalo da se desi sa našim filterom)
            // Za potrebe projekta, bacamo grešku da jasno vidimo problem.
            throw new IllegalStateException("User ID not found in Authentication details. Details object is of type: "
                    + authentication.getDetails().getClass().getName());
        }
    }
}