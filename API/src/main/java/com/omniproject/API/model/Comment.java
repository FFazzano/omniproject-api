package com.omniproject.API.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Blindagem do Texto: Não deixa salvar comentário em branco
    @NotBlank(message = "O texto do comentário não pode estar vazio.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    // 2. Blindagem do Autor: Limita os caracteres e barra strings vazias
    @NotBlank(message = "O nome do autor é obrigatório.")
    @Size(max = 100, message = "O nome do autor não pode passar de 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String autor;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore
    private Task task;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    public Comment() {
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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}