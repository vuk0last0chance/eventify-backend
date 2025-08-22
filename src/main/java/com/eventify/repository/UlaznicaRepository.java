package com.eventify.repository;

import com.eventify.entity.Ulaznica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UlaznicaRepository extends JpaRepository<Ulaznica, Long> {
    long countByDogadjajId(Long dogadjajId);
}