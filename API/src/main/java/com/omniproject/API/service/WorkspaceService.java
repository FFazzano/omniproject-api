package com.omniproject.api.service;

import com.omniproject.api.model.User;
import com.omniproject.api.model.Workspace;
import com.omniproject.api.repository.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final ActivityLogService activityLogService;

    // Injeção de dependências via construtor (recomendado pelo Spring)
    public WorkspaceService(WorkspaceRepository workspaceRepository, ActivityLogService activityLogService) {
        this.workspaceRepository = workspaceRepository;
        this.activityLogService = activityLogService;
    }

    public Workspace criarWorkspace(Workspace workspace, User usuario) {
        workspace.setUser(usuario);
        Workspace workspaceSalvo = workspaceRepository.save(workspace);
        
        String logAcao = activityLogService.formatarAcaoCriacaoWorkspace(workspaceSalvo.getNome(), usuario.getNome());
        activityLogService.registrarAcao(logAcao, usuario, workspaceSalvo, null);
        
        return workspaceSalvo;
    }

    public Workspace buscarWorkspacePorId(Long id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado."));
    }
}