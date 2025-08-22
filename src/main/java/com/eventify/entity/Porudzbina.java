package com.eventify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="porudzbine")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Porudzbina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="datum_porudzbine", nullable=false)
    private LocalDateTime datumPorudzbine;

    @Column(name="ukupan_iznos", nullable=false, precision = 10, scale = 2)
    private BigDecimal ukupanIznos;

    @Column(name="status_placanja")
    private String statusPlacanja;

    @ManyToOne(optional = false)
    @JoinColumn(name="korisnik_id", nullable=false)
    private Korisnik korisnik;

    @OneToMany(mappedBy = "porudzbina", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Ulaznica> ulaznice;
}