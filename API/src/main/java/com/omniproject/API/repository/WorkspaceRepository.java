package com.omniproject.api.repository;

import com.omniproject.api.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.omniproject.api.model.User;
import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    // O método antigo (vamos manter aqui caso você precise no futuro)
    List<Workspace> findByUser(User user);

    // --- O NOVO MÉTODO TURBINADO (MULTIPLAYER) ---
    // O comando "MEMBER OF" vasculha a lista de convidados para ver se o usuário está lá!
    @Query("SELECT DISTINCT w FROM Workspace w WHERE w.user = :usuario OR :usuario MEMBER OF w.convidados")
    List<Workspace> findMeusProjetosEConvites(@Param("usuario") User usuario);
}