package com.omniproject.API.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- Nova importação aqui
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    // --- A NOVA PARTE ESTÁ AQUI ---
    // Dizemos ao banco que Vários Workspaces podem pertencer a Um Usuário
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Evita que a senha do usuário vaze no JSON quando buscarmos o Workspace
    private User user;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    // --- CONSTRUTORES ---
    public Workspace() {
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    // --- GETTER E SETTER DO USUÁRIO (DONO) ---
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}