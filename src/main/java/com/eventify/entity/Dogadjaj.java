package com.eventify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="dogadjaji")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Dogadjaj {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String naziv;

    @Column(columnDefinition = "TEXT")
    private String opis;

    @Column(name="datum_vreme", nullable=false)
    private LocalDateTime datumVreme;

    @Column(name="lokacija_naziv")
    private String lokacijaNaziv;

    @Column(name="lokacija_adresa")
    private String lokacijaAdresa;

    @Column(name="ukupan_broj_ulaznica", nullable=false)
    private Integer ukupanBrojUlaznica;

    @Column(name="cena_ulaznice", nullable=false, precision = 10, scale = 2)
    private BigDecimal cenaUlaznice;

    @Column(name="slika_url")
    private String slikaUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name="organizator_id", nullable=false)
    private Korisnik organizator;

    @ManyToOne
    @JoinColumn(name="kategorija_id")
    private Kategorija kategorija;

    @OneToMany(mappedBy = "dogadjaj")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Ulaznica> ulaznice;
}