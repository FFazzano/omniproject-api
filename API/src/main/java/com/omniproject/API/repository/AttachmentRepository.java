package com.omniproject.api.repository;

import com.omniproject.api.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTaskId(Long taskId);

    @Modifying
    @Transactional
    void deleteByTaskId(Long taskId);
}