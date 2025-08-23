package com.eventify.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PorudzbinaRequestDTO {
    @NotNull
    private Long dogadjajId;

    @NotNull @Min(1)
    private Integer brojUlaznica;
}