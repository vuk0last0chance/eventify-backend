package com.eventify.service;

import com.eventify.dto.PorudzbinaRequestDTO;
import com.eventify.entity.Porudzbina;

import java.util.List;

public interface PorudzbinaService {
    Porudzbina createPorudzbina(PorudzbinaRequestDTO dto, Long korisnikId);
    List<Porudzbina> getPorudzbineByKorisnik(Long korisnikId);
}