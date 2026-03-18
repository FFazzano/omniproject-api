package com.omniproject.API.controller;

import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.UserRepository;
import com.omniproject.API.repository.WorkspaceRepository;
import com.omniproject.API.service.ActivityLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;
    private final ActivityLogService activityLogService;

    public WorkspaceController(WorkspaceRepository workspaceRepository,
                               ActivityLogService activityLogService) {
        this.workspaceRepository = workspaceRepository;
        this.activityLogService = activityLogService;
    }

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> criarWorkspace(@Valid @RequestBody Workspace workspace, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        workspace.setUser(usuarioLogado);

        Workspace workspaceCriado = workspaceRepository.save(workspace);

        // Registra ação no histórico
        String descricao = activityLogService.formatarAcaoCriacaoWorkspace(
                usuarioLogado.getNome(), workspaceCriado.getNome());
        activityLogService.registrarAcao(descricao, usuarioLogado, workspaceCriado, null);

        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceCriado);
    }

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

        Workspace workspaceEditado = workspaceRepository.save(workspace);

        // Registra ação no histórico
        String descricao = activityLogService.formatarAcaoEdicaoWorkspace(
                usuarioLogado.getNome(), workspaceEditado.getNome());
        activityLogService.registrarAcao(descricao, usuarioLogado, workspaceEditado, null);

        return ResponseEntity.ok(workspaceEditado);
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<?> alternarStatusWorkspace(@PathVariable Long id, Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        Workspace workspace = workspaceRepository.findById(id).orElse(null);

        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado.");
        }

        if (workspace.getUser() != null && !workspace.getUser().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Você não tem permissão para concluir este projeto.");
        }

        boolean novoStatus = !workspace.isConcluido();
        workspace.setConcluido(novoStatus);
        Workspace workspaceAtualizado = workspaceRepository.save(workspace);

        // Registra ação no histórico
        String descricao = activityLogService.formatarAcaoConclusaoWorkspace(
                usuarioLogado.getNome(), workspaceAtualizado.getNome(), novoStatus);
        activityLogService.registrarAcao(descricao, usuarioLogado, workspaceAtualizado, null);

        return ResponseEntity.ok(workspaceAtualizado);
    }

    @PostMapping("/{id}/convidar")
    public ResponseEntity<String> convidarParaProjeto(@PathVariable Long id, @RequestBody java.util.Map<String, String> requestBody, Authentication authentication) {
        String emailConvidado = requestBody.get("email");
        User donoLogado = (User) authentication.getPrincipal();

        java.util.Optional<Workspace> workspaceOpt = workspaceRepository.findById(id);
        if (workspaceOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Projeto não encontrado.");
        }
        Workspace workspace = workspaceOpt.get();

        if (!workspace.getUser().getId().equals(donoLogado.getId())) {
            return ResponseEntity.status(403).body("Acesso negado: Só o dono do projeto pode enviar convites.");
        }

        java.util.Optional<User> amigoOpt = userRepository.findByEmail(emailConvidado);
        if (amigoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário com este e-mail não encontrado no sistema.");
        }
        User amigo = amigoOpt.get();

        if (workspace.getConvidados().contains(amigo)) {
            return ResponseEntity.badRequest().body("Este usuário já é um membro do projeto!");
        }

        if (amigo.getId().equals(donoLogado.getId())) {
            return ResponseEntity.badRequest().body("Você não pode convidar a si mesmo!");
        }

        workspace.getConvidados().add(amigo);
        workspaceRepository.save(workspace);

        // Registra ação no histórico
        String descricao = activityLogService.formatarAcaoConvite(
                donoLogado.getNome(), amigo.getNome(), workspace.getNome());
        activityLogService.registrarAcao(descricao, donoLogado, workspace, null);

        return ResponseEntity.ok("Convite enviado com sucesso! " + amigo.getNome() + " agora faz parte do projeto.");
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

    @GetMapping
    public ResponseEntity<List<Workspace>> listarWorkspaces(Authentication authentication) {
        User usuarioLogado = (User) authentication.getPrincipal();
        List<Workspace> meusProjetos = workspaceRepository.findMeusProjetosEConvites(usuarioLogado);
        return ResponseEntity.ok(meusProjetos);
    }
}
