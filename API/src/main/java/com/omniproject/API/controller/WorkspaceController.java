package com.omniproject.API.controller;

import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.WorkspaceRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
@CrossOrigin(origins = "*")
public class WorkspaceController {

    // 1. Injeção por Construtor (Classe Imutável e Rápida)
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceController(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @PostMapping
    public ResponseEntity<?> criarWorkspace(@Valid @RequestBody Workspace workspace, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        workspace.setUser(usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceRepository.save(workspace));
    }

    // 2. Blindagem adicionada no Editar (@Valid)
    @PutMapping("/{id}")
    public ResponseEntity<?> editarWorkspace(@PathVariable Long id, @Valid @RequestBody Workspace workspaceAtualizado, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(id).orElse(null);

        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        if (workspace.getUser() != null && !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para editar este projeto.");
        }

        workspace.setNome(workspaceAtualizado.getNome());
        workspace.setDescricao(workspaceAtualizado.getDescricao());

        return ResponseEntity.ok(workspaceRepository.save(workspace));
    }

    // NOVO: Rota específica para Concluir / Reabrir o projeto
    @PutMapping("/{id}/concluir")
    public ResponseEntity<?> alternarStatusWorkspace(@PathVariable Long id, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(id).orElse(null);

        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        // Blindagem de segurança: Só o dono do projeto pode alterar o status!
        if (workspace.getUser() != null && !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para concluir este projeto.");
        }

        // A mágica acontece aqui: inverte o status atual (false vira true, e vice-versa)
        workspace.setConcluido(!workspace.isConcluido());

        return ResponseEntity.ok(workspaceRepository.save(workspace));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarWorkspace(@PathVariable Long id, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(id).orElse(null);

        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        if (workspace.getUser() != null && !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para deletar este projeto.");
        }

        workspaceRepository.delete(workspace);
        return ResponseEntity.ok("Projeto deletado com sucesso!");
    }

    // 3. Retorno padronizado com ResponseEntity
    @GetMapping
    public ResponseEntity<List<Workspace>> listarWorkspaces(Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        return ResponseEntity.ok(workspaceRepository.findByUser(usuarioLogado));
    }
}