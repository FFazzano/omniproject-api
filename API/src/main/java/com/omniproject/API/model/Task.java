package com.omniproject.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Anotações empilhadas e juntas (Padrão Sênior)
    @NotBlank(message = "O título da tarefa é obrigatório.")
    @Size(min = 3, max = 150, message = "O título deve ter entre 3 e 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String titulo;

    // 2. Limite de segurança para textos grandes
    @Size(max = 2000, message = "A descrição não pode passar de 2000 caracteres.")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Size(max = 50)
    @Column(length = 50)
    private String status = "PENDENTE";

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    @JsonIgnore
    private Workspace workspace;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comentarios;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Attachment> attachments;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ActivityLog> activityLogs;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;


    public Task() {
    }

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS E SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public List<Comment> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comment> comentarios) {
        this.comentarios = comentarios;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<ActivityLog> getActivityLogs() {
        return activityLogs;
    }

    public void setActivityLogs(List<ActivityLog> activityLogs) {
        this.activityLogs = activityLogs;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}