package com.omniproject.api.repository;

import com.omniproject.api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByWorkspaceId(Long workspaceId);
}