package com.omniproject.API.repository;

import com.omniproject.API.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Método mágico do Spring: Busca todos os comentários de uma tarefa específica
    List<Comment> findByTaskId(Long taskId);

}