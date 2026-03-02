package com.omniproject.API.repository;

import com.omniproject.API.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    // Só de estender o JpaRepository, o Spring já cria o Salvar, Deletar, Buscar... de graça!
}