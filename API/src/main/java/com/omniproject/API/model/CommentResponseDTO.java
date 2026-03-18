package com.omniproject.API.dto;

import com.omniproject.API.model.Comment;
import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        String texto,
        LocalDateTime dataCriacao,
        String nomeAutor // DTO achatado: Em vez de devolver o User inteiro, devolvemos apenas o nome!
) {
    public static CommentResponseDTO from(Comment comment) {
        // Como não tenho o ficheiro Comment.java no contexto, presumo que exista getUser() e getTexto()
        // Se o seu método for getUsuario() ou getAutor(), altere abaixo!
        String autor = (comment.getUser() != null) ? comment.getUser().getNome() : "Desconhecido";
        
        return new CommentResponseDTO(
                comment.getId(),
                comment.getTexto(),
                comment.getDataCriacao(),
                autor
        );
    }
}