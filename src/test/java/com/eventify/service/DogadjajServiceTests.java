package com.eventify.service;

import com.eventify.dto.DogadjajDTO;
import com.eventify.entity.Dogadjaj;
import com.eventify.entity.Korisnik;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.repository.DogadjajRepository;
import com.eventify.repository.KategorijaRepository;
import com.eventify.repository.KorisnikRepository;
import com.eventify.service.impl.DogadjajServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DogadjajServiceTests {

    @Test
    void testCreateDogadjaj_Success() {
        DogadjajRepository dr = mock(DogadjajRepository.class);
        KorisnikRepository kr = mock(KorisnikRepository.class);
        KategorijaRepository catr = mock(KategorijaRepository.class);

        DogadjajService service = new DogadjajServiceImpl(dr, kr, catr);

        when(kr.findById(1L)).thenReturn(Optional.of(Korisnik.builder().id(1L).build()));
        when(dr.save(any())).thenAnswer(inv -> {
            Dogadjaj d = inv.getArgument(0);
            d.setId(10L);
            return d;
        });

        DogadjajDTO dto = new DogadjajDTO();
        dto.setNaziv("Koncert");
        dto.setDatumVreme(LocalDateTime.now().plusDays(5));
        dto.setUkupanBrojUlaznica(100);
        dto.setCenaUlaznice(new BigDecimal("25.00"));

        DogadjajDTO res = service.createDogadjaj(dto, 1L);
        assertNotNull(res.getId());
        assertEquals("Koncert", res.getNaziv());
    }

    @Test
    void testGetDogadjajById_Success() {
        DogadjajRepository dr = mock(DogadjajRepository.class);
        KorisnikRepository kr = mock(KorisnikRepository.class);
        KategorijaRepository catr = mock(KategorijaRepository.class);

        DogadjajService service = new DogadjajServiceImpl(dr, kr, catr);

        Dogadjaj d = Dogadjaj.builder().id(5L).naziv("Event").build();
        when(dr.findById(5L)).thenReturn(Optional.of(d));

        var dto = service.getDogadjajById(5L);
        assertEquals(5L, dto.getId());
    }

    @Test
    void testGetDogadjajById_ThrowsNotFoundException() {
        DogadjajRepository dr = mock(DogadjajRepository.class);
        KorisnikRepository kr = mock(KorisnikRepository.class);
        KategorijaRepository catr = mock(KategorijaRepository.class);

        DogadjajService service = new DogadjajServiceImpl(dr, kr, catr);
        when(dr.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getDogadjajById(99L));
    }
}