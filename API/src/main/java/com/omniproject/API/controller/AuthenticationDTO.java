package com.omniproject.API.controller;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank String email,
        @NotBlank String senha // Use "senha" para bater com o seu script.js!
) {}

