package com.omniproject.API.controller;

import com.omniproject.API.model.Comment;
import com.omniproject.API.repository.CommentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    // 1. Injeção por Construtor (Padrão Sênior e Seguro)
    private final CommentRepository repository;

    public CommentController(CommentRepository repository) {
        this.repository = repository;
    }

    // 2. O Leão de Chácara (@Valid) e o Retorno 201 (Created)
    @PostMapping
    public ResponseEntity<Comment> adicionarComentario(@Valid @RequestBody Comment comment) {
        Comment comentarioSalvo = repository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(comentarioSalvo);
    }

    // 3. Retorno Profissional com Status 200 (OK)
    @GetMapping
    public ResponseEntity<List<Comment>> listarComentarios() {
        return ResponseEntity.ok(repository.findAll());
    }
}