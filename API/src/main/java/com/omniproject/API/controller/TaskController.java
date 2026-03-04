package com.omniproject.API.controller;

import com.omniproject.API.model.Task;
import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.TaskRepository;
import com.omniproject.API.repository.WorkspaceRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository; // Precisamos dele para validar o dono

    @PostMapping
    public ResponseEntity<?> criarTask(@Valid @RequestBody Task task, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();

        // 1. Busca o workspace que o usuário informou no JSON
        Workspace workspace = workspaceRepository.findById(task.getWorkspace().getId()).orElse(null);

        // 2. Regra de Negócio: O workspace existe? O usuário logado é realmente o dono dele?
        if (workspace == null || !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Erro: Workspace não encontrado ou você não tem permissão.");
        }

        // 3. Tudo certo! Amarramos a tarefa ao workspace e salvamos
        task.setWorkspace(workspace);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(task));
    }

    // Criamos uma rota específica para listar tarefas apenas de UM projeto
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<?> listarTasksDoWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);

        // A mesma checagem de segurança na hora de visualizar!
        if (workspace == null || !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Erro: Acesso negado a este projeto.");
        }

        List<Task> tasks = taskRepository.findByWorkspaceId(workspaceId);
        return ResponseEntity.ok(tasks);
    }
}