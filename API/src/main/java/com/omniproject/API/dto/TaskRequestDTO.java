package com.omniproject.API.dto;

// DTO de Requisição para criar/editar Tarefas
import jakarta.validation.constraints.NotBlank;

public record TaskRequestDTO(
        @NotBlank(message = "O título da tarefa é obrigatório.")
        String titulo,
        String descricao,
        String status,
        Long workspaceId
) {}
