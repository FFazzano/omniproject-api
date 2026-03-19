package com.omniproject.api.controller;

import com.omniproject.api.model.ActivityLog;
import com.omniproject.api.model.User;
import com.omniproject.api.model.Workspace;
import com.omniproject.api.repository.WorkspaceRepository;
import com.omniproject.api.service.ActivityLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final WorkspaceRepository workspaceRepository;

    public ActivityLogController(ActivityLogService activityLogService,
                                  WorkspaceRepository workspaceRepository) {
        this.activityLogService = activityLogService;
        this.workspaceRepository = workspaceRepository;
    }

    // ==========================================
    // MÉTODO AUXILIAR DE PERMISSÃO
    // ==========================================
    private boolean temPermissao(Workspace workspace, User usuarioLogado) {
        if (workspace == null) return false;

        boolean isDono = workspace.getUser().getId().equals(usuarioLogado.getId());
        boolean isConvidado = workspace.getConvidados() != null && workspace.getConvidados().stream()
                .anyMatch(convidado -> convidado.getId().equals(usuarioLogado.getId()));

        return isDono || isConvidado;
    }

    // ==========================================
    // ENDPOINT DE HISTÓRICO DE ATIVIDADES
    // ==========================================

    @GetMapping("/{workspaceId}/logs")
    public ResponseEntity<?> buscarLogsPorWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);

        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        if (!temPermissao(workspace, usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Erro: Você não tem permissão para visualizar o histórico deste projeto.");
        }

        List<ActivityLog> logs = activityLogService.buscarLogsPorWorkspace(workspaceId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{workspaceId}/logs/recentes")
    public ResponseEntity<?> buscarLogsRecentes(@PathVariable Long workspaceId,
                                                   @RequestParam(defaultValue = "10") int quantidade,
                                                   Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);

        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        if (!temPermissao(workspace, usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Erro: Você não tem permissão para visualizar o histórico deste projeto.");
        }

        List<ActivityLog> logs = activityLogService.buscarUltimosLogsPorWorkspace(workspaceId, quantidade);
        return ResponseEntity.ok(logs);
    }
}
