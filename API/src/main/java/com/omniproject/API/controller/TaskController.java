package com.omniproject.api.controller;

import com.omniproject.api.dto.TaskRequestDTO;
import com.omniproject.api.dto.TaskResponseDTO;
import com.omniproject.api.model.Task;
import com.omniproject.api.model.User;
import com.omniproject.api.model.Workspace;
import com.omniproject.api.repository.TaskRepository;
import com.omniproject.api.repository.WorkspaceRepository;
import com.omniproject.api.repository.AttachmentRepository;
import com.omniproject.api.repository.ActivityLogRepository;
import com.omniproject.api.service.ActivityLogService;
import com.omniproject.api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ActivityLogService activityLogService;
    private final AttachmentRepository attachmentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final TaskService taskService;

    public TaskController(TaskRepository taskRepository,
                          WorkspaceRepository workspaceRepository,
                          ActivityLogService activityLogService,
                          AttachmentRepository attachmentRepository,
                          ActivityLogRepository activityLogRepository,
                          TaskService taskService) {
        this.taskRepository = taskRepository;
        this.workspaceRepository = workspaceRepository;
        this.activityLogService = activityLogService;
        this.attachmentRepository = attachmentRepository;
        this.activityLogRepository = activityLogRepository;
        this.taskService = taskService;
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
    // ROTAS (ENDPOINTS)
    // ==========================================

    @PostMapping
    public ResponseEntity<?> criarTask(@Valid @RequestBody TaskRequestDTO taskDTO, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(taskDTO.workspaceId()).orElse(null);

        if (!temPermissao(workspace, usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Workspace não encontrado ou permissão negada.");
        }

        Task task = new Task();
        task.setTitulo(taskDTO.titulo());
        task.setDescricao(taskDTO.descricao());
        if (taskDTO.status() != null) task.setStatus(taskDTO.status());
        
        if (taskDTO.tipoRecorrencia() != null) {
            task.setTipoRecorrencia(taskDTO.tipoRecorrencia());
        }
        
        task.setWorkspace(workspace);
        Task taskCriada = taskRepository.save(task);

        // Registra ação no histórico
        String descricao = activityLogService.formatarAcaoCriacaoTarefa(
                taskCriada.getTitulo(), usuarioLogado.getNome());
        activityLogService.registrarAcao(descricao, usuarioLogado, workspace, taskCriada);

        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponseDTO.from(taskCriada));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<TaskResponseDTO>> listarTasksDoWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);

        // Log solicitado para ajudar no debug
        System.out.println(">>> Usuário logado tentando acessar tarefas: " + usuarioLogado.getEmail());

        if (!temPermissao(workspace, usuarioLogado)) {
            System.out.println(">>> ERRO: Usuário " + usuarioLogado.getEmail() + " bloqueado. Workspace ID " + workspaceId + " não existe ou não pertence a ele.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TaskResponseDTO> tasks = taskRepository.findByWorkspaceId(workspaceId).stream()
                .map(TaskResponseDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO taskAtualizada, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        if (!temPermissao(task.getWorkspace(), usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para alterar esta tarefa.");
        }

        String statusAnterior = task.getStatus();
        Task taskSalva = task;

        if (taskAtualizada.titulo() != null) {
            task.setTitulo(taskAtualizada.titulo());
            taskSalva = taskRepository.save(task);
        }

        if (taskAtualizada.tipoRecorrencia() != null) {
            task.setTipoRecorrencia(taskAtualizada.tipoRecorrencia());
            taskSalva = taskRepository.save(task);
        }

        if (taskAtualizada.status() != null && !taskAtualizada.status().equals(statusAnterior)) {
            
            // Passa para o Service para que a regra de negócio (Recorrência e Notificação) seja executada!
            taskSalva = taskService.atualizarStatus(id, taskAtualizada.status(), usuarioLogado);

            String descricao = activityLogService.formatarAcaoAtualizacaoStatus(
                    taskSalva.getTitulo(), statusAnterior, taskSalva.getStatus(), usuarioLogado.getNome());
            activityLogService.registrarAcao(descricao, usuarioLogado, taskSalva.getWorkspace(), taskSalva);
        }

        return ResponseEntity.ok(TaskResponseDTO.from(taskSalva));
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

        // Registra ação antes de excluir (para manter a referência da task)
        String descricao = activityLogService.formatarAcaoExclusaoTarefa(
                task.getTitulo(), usuarioLogado.getNome());
        // Passamos 'null' na task para que ESTE log em específico não seja apagado pela limpeza abaixo!
        activityLogService.registrarAcao(descricao, usuarioLogado, task.getWorkspace(), null);

        // Deleção Manual Segura das dependências (evita o Erro 500 de Foreign Key)
        attachmentRepository.deleteByTaskId(id);
        activityLogRepository.deleteByTaskId(id);

        taskRepository.delete(task);
        return ResponseEntity.ok("Tarefa deletada com sucesso!");
    }
}
