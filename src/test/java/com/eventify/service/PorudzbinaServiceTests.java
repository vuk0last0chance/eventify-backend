package com.eventify.service;

import com.eventify.dto.PorudzbinaRequestDTO;
import com.eventify.entity.Dogadjaj;
import com.eventify.entity.Korisnik;
import com.eventify.entity.Porudzbina;
import com.eventify.exception.NotEnoughTicketsException;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.repository.DogadjajRepository;
import com.eventify.repository.KorisnikRepository;
import com.eventify.repository.PorudzbinaRepository;
import com.eventify.repository.UlaznicaRepository;
import eventify.repository.*;
import com.eventify.service.impl.PorudzbinaServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PorudzbinaServiceTests {

    @Test
    void testCreatePorudzbina_Success() {
        DogadjajRepository dr = mock(DogadjajRepository.class);
        KorisnikRepository kr = mock(KorisnikRepository.class);
        PorudzbinaRepository pr = mock(PorudzbinaRepository.class);
        UlaznicaRepository ur = mock(UlaznicaRepository.class);

        PorudzbinaService service = new PorudzbinaServiceImpl(dr, kr, pr, ur);

        Dogadjaj d = Dogadjaj.builder()
                .id(1L)
                .ukupanBrojUlaznica(100)
                .cenaUlaznice(new BigDecimal("10.00"))
                .build();

        when(dr.findById(1L)).thenReturn(Optional.of(d));
        when(kr.findById(7L)).thenReturn(Optional.of(Korisnik.builder().id(7L).build()));
        when(ur.countByDogadjajId(1L)).thenReturn(10L);
        when(pr.save(any())).thenAnswer(inv -> {
            Porudzbina p = inv.getArgument(0);
            p.setId(50L);
            return p;
        });

        PorudzbinaRequestDTO dto = new PorudzbinaRequestDTO();
        dto.setDogadjajId(1L);
        dto.setBrojUlaznica(2);

        Porudzbina p = service.createPorudzbina(dto, 7L);
        assertEquals(new BigDecimal("20.00"), p.getUkupanIznos());
    }

    @Test
    void testCreatePorudzbina_ThrowsException_WhenNotEnoughTickets() {
        DogadjajRepository dr = mock(DogadjajRepository.class);
        KorisnikRepository kr = mock(KorisnikRepository.class);
        PorudzbinaRepository pr = mock(PorudzbinaRepository.class);
        UlaznicaRepository ur = mock(UlaznicaRepository.class);

        PorudzbinaService service = new PorudzbinaServiceImpl(dr, kr, pr, ur);

        Dogadjaj d = Dogadjaj.builder().id(1L).ukupanBrojUlaznica(10).cenaUlaznice(new BigDecimal("10.00")).build();
        when(dr.findById(1L)).thenReturn(Optional.of(d));
        when(kr.findById(7L)).thenReturn(Optional.of(Korisnik.builder().id(7L).build()));
        when(ur.countByDogadjajId(1L)).thenReturn(9L);

        PorudzbinaRequestDTO dto = new PorudzbinaRequestDTO();
        dto.setDogadjajId(1L);
        dto.setBrojUlaznica(5);

        assertThrows(NotEnoughTicketsException.class, () -> service.createPorudzbina(dto, 7L));
    }

    @Test
    void testCreatePorudzbina_ThrowsException_WhenEventNotFound() {
        DogadjajRepository dr = mock(DogadjajRepository.class);
        KorisnikRepository kr = mock(KorisnikRepository.class);
        PorudzbinaRepository pr = mock(PorudzbinaRepository.class);
        UlaznicaRepository ur = mock(UlaznicaRepository.class);

        PorudzbinaService service = new PorudzbinaServiceImpl(dr, kr, pr, ur);
        when(dr.findById(100L)).thenReturn(Optional.empty());

        PorudzbinaRequestDTO dto = new PorudzbinaRequestDTO();
        dto.setDogadjajId(100L);
        dto.setBrojUlaznica(1);

        assertThrows(ResourceNotFoundException.class, () -> service.createPorudzbina(dto, 1L));
    }
}