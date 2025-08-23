package com.eventify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String lozinka;
}