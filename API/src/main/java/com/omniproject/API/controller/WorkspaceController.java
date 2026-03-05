package com.omniproject.API.controller;

import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // <-- Importação do Security
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
@CrossOrigin(origins = "*")
public class WorkspaceController {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    // --- ROTA DE CRIAR PROJETO ---
    @PostMapping
    public ResponseEntity<?> criarWorkspace(@RequestBody Workspace workspace, Authentication authentication) {
        // 1. Descobre quem é o dono da Pulseira VIP
        User usuarioLogado = (User) authentication.getPrincipal();

        // 2. CARIMBO DE SEGURANÇA: Avisa que este projeto pertence a este usuário!
        workspace.setUser(usuarioLogado);

        // 3. Salva no banco de dados
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceRepository.save(workspace));
    }

    // --- ROTA DE ATUALIZAR (EDITAR) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> editarWorkspace(@PathVariable Long id, @RequestBody Workspace workspaceAtualizado, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(id).orElse(null);

        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        // Verifica se o projeto TEM dono, e se o dono NÃO é o usuário logado
        if (workspace.getUser() != null && !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Erro: Você não tem permissão para editar este projeto.");
        }

        workspace.setNome(workspaceAtualizado.getNome());
        workspace.setDescricao(workspaceAtualizado.getDescricao());

        return ResponseEntity.ok(workspaceRepository.save(workspace));
    }

    // --- ROTA DE DELETAR ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarWorkspace(@PathVariable Long id, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(id).orElse(null);

        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        // A MÁGICA AQUI: Se for fantasma (getUser() == null), ele pula esse IF e deixa deletar!
        if (workspace.getUser() != null && !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Erro: Você não tem permissão para deletar este projeto.");
        }

        workspaceRepository.delete(workspace);

        return ResponseEntity.ok("Projeto deletado com sucesso!");
    }

    @GetMapping
    public List<Workspace> listarWorkspaces(Authentication authentication) {
        // Pega o usuário logado e busca no banco SÓ os projetos dele!
        User usuarioLogado = (User) authentication.getPrincipal();
        return workspaceRepository.findByUser(usuarioLogado);
    }
}