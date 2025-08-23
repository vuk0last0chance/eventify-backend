package com.eventify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UserInfoDTO user;
}