package com.omniproject.api.dto;

// DTO de Resposta para os Anexos
import com.omniproject.api.model.Attachment;

public record AttachmentResponseDTO(
        Long id,
        String fileName,
        String fileType
) {
    public static AttachmentResponseDTO from(Attachment attachment) {
        return new AttachmentResponseDTO(
                attachment.getId(), attachment.getFileName(), attachment.getFileType()
        );
    }
}
