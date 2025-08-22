package com.eventify.repository;

import com.eventify.entity.Porudzbina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PorudzbinaRepository extends JpaRepository<Porudzbina, Long> {
    List<Porudzbina> findByKorisnikIdOrderByDatumPorudzbineDesc(Long korisnikId);
}