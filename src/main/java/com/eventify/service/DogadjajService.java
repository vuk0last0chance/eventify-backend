package com.eventify.service;

import com.eventify.dto.DogadjajDTO;

import java.time.LocalDate;
import java.util.List;

public interface DogadjajService {
    DogadjajDTO createDogadjaj(DogadjajDTO dto, Long organizatorId);
    DogadjajDTO getDogadjajById(Long id);
    List<DogadjajDTO> getAllDogadjaji();
    List<DogadjajDTO> searchDogadjaji(String grad, LocalDate datum, Long kategorijaId);
    DogadjajDTO updateDogadjaj(Long id, DogadjajDTO dto, Long requestingUserId, boolean isAdmin);
    void deleteDogadjaj(Long id, Long requestingUserId, boolean isAdmin);
}