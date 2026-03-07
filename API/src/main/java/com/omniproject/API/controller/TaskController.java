package com.omniproject.API.controller;

import com.omniproject.API.model.Task;
import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.TaskRepository;
import com.omniproject.API.repository.WorkspaceRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    // 1. Injeção por Construtor Segura e Imutável
    private final TaskRepository taskRepository;
    private final WorkspaceRepository workspaceRepository;

    public TaskController(TaskRepository taskRepository, WorkspaceRepository workspaceRepository) {
        this.taskRepository = taskRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @PostMapping
    public ResponseEntity<?> criarTask(@Valid @RequestBody Task task, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(task.getWorkspace().getId()).orElse(null);

        if (workspace == null || !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Workspace não encontrado ou permissão negada.");
        }

        task.setWorkspace(workspace);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(task));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<?> listarTasksDoWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);

        if (workspace == null || !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Acesso negado a este projeto.");
        }

        List<Task> tasks = taskRepository.findByWorkspaceId(workspaceId);
        return ResponseEntity.ok(tasks);
    }

    // 2. Blindagem adicionada aqui (@Valid)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarTask(@PathVariable Long id, @Valid @RequestBody Task taskAtualizada, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        if (!task.getWorkspace().getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para alterar esta tarefa.");
        }

        task.setTitulo(taskAtualizada.getTitulo());
        task.setConcluida(taskAtualizada.isConcluida());

        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarTask(@PathVariable Long id, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        if (!task.getWorkspace().getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para deletar esta tarefa.");
        }

        taskRepository.delete(task);
        return ResponseEntity.ok("Tarefa deletada com sucesso!");
    }
}