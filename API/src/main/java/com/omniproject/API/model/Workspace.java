package com.omniproject.API.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do projeto é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String nome;

    @Size(max = 255, message = "A descrição não pode passar de 255 caracteres.")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    // O dono original do projeto continua existindo (não mexa nele!)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // --- NOVA RELAÇÃO: OS CONVIDADOS ---
    // Cria uma tabela invisível no banco só para guardar quem foi convidado para qual projeto
    @ManyToMany
    @JoinTable(
            name = "workspace_convidados",
            joinColumns = @JoinColumn(name = "workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> convidados = new HashSet<>();

    // --- SALA DE ESPERA (CONVITES PENDENTES) ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workspace_convites_pendentes",
            joinColumns = @JoinColumn(name = "workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> convitesPendentes = new HashSet<>();

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ActivityLog> activityLogs;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    @Column(name = "concluido", columnDefinition = "boolean default false")
    private boolean concluido = false; // Por padrão, todo projeto nasce "Aberto"

    public Workspace() {
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

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // O Getter e Setter que faltavam para as tarefas!
    public Set<Task> getTasks() { return tasks; }
    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }

    public List<ActivityLog> getActivityLogs() {
        return activityLogs;
    }

    public void setActivityLogs(List<ActivityLog> activityLogs) {
        this.activityLogs = activityLogs;
    }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    public Set<User> getConvidados() {
        return convidados;
    }

    public void setConvidados(Set<User> convidados) {
        this.convidados = convidados;
    }

    public Set<User> getConvitesPendentes() {
        return convitesPendentes;
    }

    public void setConvitesPendentes(Set<User> convitesPendentes) {
        this.convitesPendentes = convitesPendentes;
    }
}