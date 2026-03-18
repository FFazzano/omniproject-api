package com.omniproject.API.dto;

import com.omniproject.API.model.Task;
import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        String titulo,
        String descricao,
        String status,
        Long workspaceId,
        LocalDateTime criadoEm
) {
    public static TaskResponseDTO from(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitulo(),
                task.getDescricao(),
                task.getStatus(),
                task.getWorkspace() != null ? task.getWorkspace().getId() : null,
                task.getCriadoEm()
        );
    }
}