package com.omniproject.API.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título da tarefa é obrigatório e não pode estar em branco")
    @Size(min = 3, max = 150, message = "O título deve ter entre 3 e 150 caracteres")

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 50)
    private String status = "PENDENTE";

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    // --- LISTA DE COMENTÁRIOS ---
    @OneToMany(mappedBy = "task")
    private List<Comment> comentarios;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    // --- CONSTRUTORES ---
    public Task() {
    }

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    // --- GETTERS E SETTERS ---
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

    // --- GETTER E SETTER DA LISTA DE COMENTÁRIOS ---
    public List<Comment> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comment> comentarios) {
        this.comentarios = comentarios;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}