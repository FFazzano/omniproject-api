package com.omniproject.api.dto;

import java.time.LocalDate;
import java.util.List;

public class TrixResponseDTO {
    private String nomeProjeto;
    private LocalDate dataVencimento;
    private List<String> tarefas;

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public List<String> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<String> tarefas) {
        this.tarefas = tarefas;
    }
}