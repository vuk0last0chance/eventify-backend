package com.eventify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="kategorije")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Kategorija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, unique = true, length = 100)
    private String naziv;

    @OneToMany(mappedBy = "kategorija")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Dogadjaj> dogadjaji;
}