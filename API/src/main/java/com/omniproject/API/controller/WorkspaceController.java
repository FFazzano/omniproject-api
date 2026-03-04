package com.omniproject.API.controller;

import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // <-- Importação do Security
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

    @Autowired
    private WorkspaceRepository repository;

    @PostMapping
    public Workspace criarWorkspace(@RequestBody Workspace workspace, Authentication authentication) {
        // Pega o usuário que está logado no momento (através do Token JWT)
        User usuarioLogado = (User) authentication.getPrincipal();

        // Define que este usuário é o dono do novo workspace
        workspace.setUser(usuarioLogado);

        return repository.save(workspace);
    }

    @GetMapping
    public List<Workspace> listarWorkspaces(Authentication authentication) {
        // Pega o usuário logado e busca no banco SÓ os projetos dele!
        User usuarioLogado = (User) authentication.getPrincipal();
        return repository.findByUser(usuarioLogado);
    }
}