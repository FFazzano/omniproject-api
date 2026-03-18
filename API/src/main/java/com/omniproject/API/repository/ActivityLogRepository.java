package com.omniproject.API.repository;

import com.omniproject.API.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @Query("SELECT al FROM ActivityLog al " +
           "LEFT JOIN FETCH al.usuario " +
           "LEFT JOIN FETCH al.task " +
           "WHERE al.workspace.id = :workspaceId " +
           "ORDER BY al.dataHora DESC")
    List<ActivityLog> findByWorkspaceIdOrderByDataHoraDesc(@Param("workspaceId") Long workspaceId);

    List<ActivityLog> findTop10ByWorkspaceIdOrderByDataHoraDesc(Long workspaceId);

    @Modifying
    @Transactional
    void deleteByTaskId(Long taskId);
}
