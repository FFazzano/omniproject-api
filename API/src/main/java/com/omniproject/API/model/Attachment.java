package com.omniproject.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARBINARY)
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public Attachment() {}

    public Attachment(String fileName, String fileType, byte[] data, Task task) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.task = task;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
}