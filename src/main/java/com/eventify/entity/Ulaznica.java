package com.eventify.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="ulaznice")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Ulaznica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="jedinstveni_kod", nullable=false, unique = true)
    private String jedinstveniKod;

    @Column(nullable=false)
    private String status; // VALID, USED

    @ManyToOne(optional = false)
    @JoinColumn(name="porudzbina_id", nullable=false)
    private Porudzbina porudzbina;

    @ManyToOne(optional = false)
    @JoinColumn(name="dogadjaj_id", nullable=false)
    private Dogadjaj dogadjaj;
}