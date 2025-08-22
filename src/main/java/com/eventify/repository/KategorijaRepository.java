package com.eventify.repository;

import com.eventify.entity.Kategorija;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KategorijaRepository extends JpaRepository<Kategorija, Integer> {
    Optional<Kategorija> findByNaziv(String naziv);
}