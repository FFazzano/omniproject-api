package com.omniproject.api.dto;

// DTO de Resposta para envio de Tarefas ao Front-end
import com.omniproject.api.model.Task;
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
