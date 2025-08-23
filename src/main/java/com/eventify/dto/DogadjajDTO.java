package com.eventify.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DogadjajDTO {
    private Long id;

    @NotBlank
    private String naziv;

    private String opis;

    @NotNull
    private LocalDateTime datumVreme;

    private String lokacijaNaziv;
    private String lokacijaAdresa;

    @NotNull @Min(1)
    private Integer ukupanBrojUlaznica;

    @NotNull @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal cenaUlaznice;

    private String slikaUrl;

    private Integer kategorijaId; // Nullable
}