package com.eventify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "korisnici")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Korisnik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String ime;

    @Column(nullable=false, unique = true)
    private String email;

    @Column(name="lozinka_hash", nullable=false)
    private String lozinkaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Uloga uloga;

    @OneToMany(mappedBy = "organizator")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Dogadjaj> dogadjaji;

    @OneToMany(mappedBy = "korisnik")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Porudzbina> porudzbine;
}