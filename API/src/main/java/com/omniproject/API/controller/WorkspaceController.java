package com.omniproject.API.controller;

import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

    @Autowired
    private WorkspaceRepository repository;

    // Rota para CRIAR um novo workspace (POST)
    @PostMapping
    public Workspace criarWorkspace(@RequestBody Workspace workspace) {
        return repository.save(workspace);
    }

    // Rota para LISTAR todos os workspaces (GET)
    @GetMapping
    public List<Workspace> listarWorkspaces() {
        return repository.findAll();
    }
}