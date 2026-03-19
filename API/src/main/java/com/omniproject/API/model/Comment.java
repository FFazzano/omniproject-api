package com.omniproject.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O comentário não pode ser vazio.")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    // Relacionamento: Vários Comentários pertencem a Uma Tarefa
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore // Evita o loop infinito na hora de gerar o JSON
    private Task task;

    // Relacionamento com o Usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment() {
    }

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS E SETTERS
    // ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}