package com.omniproject.API.dto;

import com.omniproject.API.model.Comment;
import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        String texto,
        LocalDateTime dataCriacao,
        String nomeAutor // DTO achatado: Em vez de devolver o User inteiro, devolvemos apenas o nome!
) {
    // Construtor que aceita a entidade Comment diretamente
    public CommentResponseDTO(Comment comment) {
        this(
            comment.getId(),
            comment.getTexto(),
            comment.getDataCriacao(),
            (comment.getUser() != null) ? comment.getUser().getNome() : "Desconhecido"
        );
    }
}