package com.omniproject.api.dto;

// DTO de Requisição para criar/editar Tarefas
import jakarta.validation.constraints.NotBlank;
import com.omniproject.api.model.TipoRecorrencia;

public record TaskRequestDTO(
        @NotBlank(message = "O título da tarefa é obrigatório.")
        String titulo,
        String descricao,
        String status,
        Long workspaceId,
        TipoRecorrencia tipoRecorrencia
) {}
