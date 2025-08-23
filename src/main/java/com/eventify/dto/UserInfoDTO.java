package com.eventify.dto;

import com.eventify.entity.Uloga;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String ime;
    private String email;
    private Uloga uloga;
}