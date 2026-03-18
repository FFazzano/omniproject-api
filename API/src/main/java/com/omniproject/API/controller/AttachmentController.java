package com.omniproject.API.controller;

import com.omniproject.API.dto.AttachmentResponseDTO;
import com.omniproject.API.model.Attachment;
import com.omniproject.API.model.Task;
import com.omniproject.API.repository.AttachmentRepository;
import com.omniproject.API.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class AttachmentController {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private TaskRepository taskRepository;

    // 1. Upload do Arquivo amarrado a uma Tarefa
    @PostMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<?> uploadFile(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isEmpty()) return ResponseEntity.notFound().build();

            Attachment attachment = new Attachment(file.getOriginalFilename(), file.getContentType(), file.getBytes(), taskOptional.get());
            attachmentRepository.save(attachment);

            return ResponseEntity.status(HttpStatus.CREATED).body("Arquivo salvo com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar arquivo.");
        }
    }

    // 2. Listar Anexos da Tarefa (O DTO garante que os bytes gigantes não sejam trafegados na lista)
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDTO>> listarAnexos(@PathVariable Long taskId) {
        List<AttachmentResponseDTO> res = attachmentRepository.findByTaskId(taskId).stream()
                .map(AttachmentResponseDTO::from).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    // 3. Download/Visualização do Arquivo (Devolve os bytes reais)
    @GetMapping("/attachments/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        Optional<Attachment> anexoOpt = attachmentRepository.findById(id);
        if (anexoOpt.isEmpty()) return ResponseEntity.notFound().build();
        
        Attachment anexo = anexoOpt.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + anexo.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, anexo.getFileType())
                .body(anexo.getData());
    }
}