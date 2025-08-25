package com.eventify.service.impl;

import com.eventify.dto.PorudzbinaRequestDTO;
import com.eventify.entity.Dogadjaj;
import com.eventify.entity.Korisnik;
import com.eventify.entity.Porudzbina;
import com.eventify.entity.Ulaznica;
import com.eventify.exception.NotEnoughTicketsException;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.repository.DogadjajRepository;
import com.eventify.repository.KorisnikRepository;
import com.eventify.repository.PorudzbinaRepository;
import com.eventify.repository.UlaznicaRepository;
import com.eventify.repository.*;
import com.eventify.service.PorudzbinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PorudzbinaServiceImpl implements PorudzbinaService {

    private final DogadjajRepository dogadjajRepository;
    private final KorisnikRepository korisnikRepository;
    private final PorudzbinaRepository porudzbinaRepository;
    private final UlaznicaRepository ulaznicaRepository;

    @Override
    @Transactional
    public Porudzbina createPorudzbina(PorudzbinaRequestDTO dto, Long korisnikId) {
        Dogadjaj dogadjaj = dogadjajRepository.findById(dto.getDogadjajId())
                .orElseThrow(() -> new ResourceNotFoundException("Događaj nije pronađen"));

        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        long sold = ulaznicaRepository.countByDogadjajId(dogadjaj.getId());
        long available = dogadjaj.getUkupanBrojUlaznica() - sold;
        if (dto.getBrojUlaznica() > available) {
            throw new NotEnoughTicketsException("Nema dovoljno dostupnih ulaznica.");
        }

        BigDecimal total = dogadjaj.getCenaUlaznice()
                .multiply(BigDecimal.valueOf(dto.getBrojUlaznica()));

        Porudzbina p = Porudzbina.builder()
                .datumPorudzbine(LocalDateTime.now())
                .ukupanIznos(total)
                .statusPlacanja("COMPLETED") // simulate successful payment
                .korisnik(korisnik)
                .build();

        p = porudzbinaRepository.save(p);

        List<Ulaznica> ulaznice = new ArrayList<>();
        for (int i = 0; i < dto.getBrojUlaznica(); i++) {
            Ulaznica u = Ulaznica.builder()
                    .jedinstveniKod(UUID.randomUUID().toString())
                    .status("VALID")
                    .porudzbina(p)
                    .dogadjaj(dogadjaj)
                    .build();
            ulaznice.add(u);
        }
        ulaznicaRepository.saveAll(ulaznice);
        p.setUlaznice(ulaznice);

        return p;
    }

    @Override
    public List<Porudzbina> getPorudzbineByKorisnik(Long korisnikId) {
        return porudzbinaRepository.findByKorisnikIdOrderByDatumPorudzbineDesc(korisnikId);
    }
}