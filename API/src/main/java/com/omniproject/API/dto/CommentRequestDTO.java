package com.omniproject.API.dto;

// DTO de Requisição para novos Comentários
import jakarta.validation.constraints.NotBlank;

public record CommentRequestDTO(
        @NotBlank(message = "O comentário não pode estar vazio.")
        String texto
) {}
