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

    private final TaskRepository taskRepository;
    private final WorkspaceRepository workspaceRepository;

    public TaskController(TaskRepository taskRepository, WorkspaceRepository workspaceRepository) {
        this.taskRepository = taskRepository;
        this.workspaceRepository = workspaceRepository;
    }

    // ==========================================
    // MÉTODO AUXILIAR DE PERMISSÃO (Boas Práticas OOP)
    // ==========================================
    private boolean temPermissao(Workspace workspace, User usuarioLogado) {
        if (workspace == null) return false;

        // 1. É o dono absoluto do projeto?
        boolean isDono = workspace.getUser().getId().equals(usuarioLogado.getId());

        // 2. É um convidado do projeto? (Verifica se a lista não é nula e procura o ID)
        boolean isConvidado = workspace.getConvidados() != null && workspace.getConvidados().stream()
                .anyMatch(convidado -> convidado.getId().equals(usuarioLogado.getId()));

        // Retorna true se for dono OU convidado
        return isDono || isConvidado;
    }

    // ==========================================
    // ROTAS (ENDPOINTS)
    // ==========================================

    @PostMapping
    public ResponseEntity<?> criarTask(@Valid @RequestBody Task task, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(task.getWorkspace().getId()).orElse(null);

        // Usa o método auxiliar para barrar intrusos
        if (!temPermissao(workspace, usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Workspace não encontrado ou permissão negada.");
        }

        task.setWorkspace(workspace);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(task));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<?> listarTasksDoWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);

        if (!temPermissao(workspace, usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Acesso negado a este projeto.");
        }

        List<Task> tasks = taskRepository.findByWorkspaceId(workspaceId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarTask(@PathVariable Long id, @Valid @RequestBody Task taskAtualizada, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        if (!temPermissao(task.getWorkspace(), usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para alterar esta tarefa.");
        }

        if (taskAtualizada.getTitulo() != null) {
            task.setTitulo(taskAtualizada.getTitulo());
        }

        if (taskAtualizada.getStatus() != null) {
            task.setStatus(taskAtualizada.getStatus());
        }

        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarTask(@PathVariable Long id, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        if (!temPermissao(task.getWorkspace(), usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para deletar esta tarefa.");
        }

        taskRepository.delete(task);
        return ResponseEntity.ok("Tarefa deletada com sucesso!");
    }
}