package com.omniproject.API.controller;

import com.omniproject.API.model.Comment;
import com.omniproject.API.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository repository;

    @PostMapping
    public Comment adicionarComentario(@RequestBody Comment comment) {
        return repository.save(comment);
    }

    @GetMapping
    public List<Comment> listarComentarios() {
        return repository.findAll();
    }
}