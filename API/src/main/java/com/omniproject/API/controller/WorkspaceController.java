package com.omniproject.API.controller;

import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.UserRepository;
import com.omniproject.API.repository.WorkspaceRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserRepository userRepository;

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

    @PostMapping("/{id}/convidar")
    public ResponseEntity<String> convidarParaProjeto(@PathVariable Long id, @RequestBody java.util.Map<String, String> requestBody, org.springframework.security.core.Authentication authentication) {
        String emailConvidado = requestBody.get("email");

        // 1. Descobre quem é o dono (quem está fazendo a requisição)
        User donoLogado = (User) authentication.getPrincipal();

        // 2. Busca o projeto no banco
        java.util.Optional<Workspace> workspaceOpt = workspaceRepository.findById(id);
        if (workspaceOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Projeto não encontrado.");
        }
        Workspace workspace = workspaceOpt.get();

        // 3. Trava de Segurança: Só o dono do projeto pode convidar outras pessoas!
        if (!workspace.getUser().getId().equals(donoLogado.getId())) {
            return ResponseEntity.status(403).body("Acesso negado: Só o dono do projeto pode enviar convites.");
        }

        // 4. Busca o amigo pelo e-mail usando o Optional
        java.util.Optional<User> amigoOpt = userRepository.findByEmail(emailConvidado);

        if (amigoOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário com este e-mail não encontrado no sistema.");
        }

        // Abre a "caixa" do Optional e pega o usuário de verdade
        User amigo = amigoOpt.get();

        // 5. Verifica se o amigo já não está no projeto para não duplicar
        if (workspace.getConvidados().contains(amigo)) {
            return ResponseEntity.badRequest().body("Este usuário já é um membro do projeto!");
        }

        // Impede o dono de convidar a si mesmo (seria estranho, né?)
        if (amigo.getId().equals(donoLogado.getId())) {
            return ResponseEntity.badRequest().body("Você não pode convidar a si mesmo!");
        }

        // Impede de convidar a si mesmo
        if (amigo.getId().equals(donoLogado.getId())) {
            return ResponseEntity.badRequest().body("Você não pode convidar a si mesmo!");
        }

        // 6. Mágica do Hibernate: Adiciona na lista e salva
        workspace.getConvidados().add(amigo);
        workspaceRepository.save(workspace);

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

    // 3. Retorno padronizado com ResponseEntity
    @GetMapping
    public ResponseEntity<List<Workspace>> listarWorkspaces(org.springframework.security.core.Authentication authentication) {
        // Pega quem é a pessoa que está logada agora (ex: Vinicius)
        User usuarioLogado = (User) authentication.getPrincipal();

        // Antes devia estar algo como findByUser, agora usamos a nossa nova super-query:
        List<Workspace> meusProjetos = workspaceRepository.findMeusProjetosEConvites(usuarioLogado);

        return ResponseEntity.ok(meusProjetos);
    }
}

