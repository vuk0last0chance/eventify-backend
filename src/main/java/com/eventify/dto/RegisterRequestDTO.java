package com.eventify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank @Size(max = 255)
    private String ime;

    @NotBlank @Email @Size(max = 255)
    private String email;

    // At least 8 chars, one digit, one lowercase, one uppercase
    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Lozinka mora imati najmanje 8 karaktera, 1 cifru, 1 malo i 1 veliko slovo.")
    private String lozinka;
}