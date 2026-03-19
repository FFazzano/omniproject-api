package com.omniproject.api.controller;

import com.omniproject.api.dto.CommentRequestDTO;
import com.omniproject.api.dto.CommentResponseDTO;
import com.omniproject.api.model.Comment;
import com.omniproject.api.model.Task;
import com.omniproject.api.model.User;
import com.omniproject.api.repository.CommentRepository;
import com.omniproject.api.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks/{taskId}/comments") // Rota RESTful nível Sênior
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    // 1. Busca todos os comentários de uma tarefa específica
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> listarComentarios(@PathVariable Long taskId) {
        List<CommentResponseDTO> comentarios = commentRepository.findByTaskId(taskId).stream()
                .map(CommentResponseDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(comentarios);
    }

    // 2. Adiciona um novo comentário na tarefa
    @PostMapping
    public ResponseEntity<?> adicionarComentario(@PathVariable Long taskId, @Valid @RequestBody CommentRequestDTO dto, Authentication authentication) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isEmpty()) {
            // Se a tarefa não existir, devolvemos um erro 404 limpo
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        Comment comment = new Comment();
        comment.setTexto(dto.texto());
        Task task = taskOptional.get();
        comment.setTask(task); // Amarra o comentário à tarefa antes de salvar
        
        if(authentication != null && authentication.getPrincipal() instanceof User) {
            comment.setUser((User) authentication.getPrincipal());
        }

        Comment salvo = commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentResponseDTO(salvo));
    }
}