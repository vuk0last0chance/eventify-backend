package com.eventify.controller;

import com.eventify.dto.DogadjajDTO;
import com.eventify.entity.Uloga;
import com.eventify.service.DogadjajService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dogadjaji")
@RequiredArgsConstructor
public class DogadjajController {

    private final DogadjajService dogadjajService;

    @GetMapping
    public ResponseEntity<List<DogadjajDTO>> getAll() {
        return ResponseEntity.ok(dogadjajService.getAllDogadjaji());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DogadjajDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dogadjajService.getDogadjajById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DogadjajDTO>> search(
            @RequestParam(required = false) String grad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum,
            @RequestParam(required = false) Long kategorijaId
    ) {
        return ResponseEntity.ok(dogadjajService.searchDogadjaji(grad, datum, kategorijaId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATOR')")
    public ResponseEntity<DogadjajDTO> create(@Valid @RequestBody DogadjajDTO dto, Authentication auth) {
        Long organizatorId = (Long) auth.getDetails();
        return ResponseEntity.ok(dogadjajService.createDogadjaj(dto, organizatorId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZATOR','ADMIN')")
    public ResponseEntity<DogadjajDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody DogadjajDTO dto,
                                              Authentication auth) {
        Long userId = (Long) auth.getDetails();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + Uloga.ADMIN.name()));
        return ResponseEntity.ok(dogadjajService.updateDogadjaj(id, dto, userId, isAdmin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZATOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getDetails();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + Uloga.ADMIN.name()));
        dogadjajService.deleteDogadjaj(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }
}