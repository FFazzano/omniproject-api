package com.omniproject.api.service;

import com.omniproject.api.model.Task;
import com.omniproject.api.model.TipoRecorrencia;
import com.omniproject.api.model.User;
import com.omniproject.api.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final NotificacaoService notificacaoService;

    public TaskService(TaskRepository taskRepository, NotificacaoService notificacaoService) {
        this.taskRepository = taskRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public Task atualizarStatus(Long taskId, String novoStatus, User usuarioLogado) {
        Task taskAtual = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
            
        taskAtual.setStatus(novoStatus);

        // Lógica de Recorrência: Ao concluir, agenda a próxima execução com base no Enum
        if ("CONCLUIDA".equalsIgnoreCase(novoStatus) 
                && taskAtual.getTipoRecorrencia() != TipoRecorrencia.NENHUMA
                && !taskAtual.getWorkspace().isConcluido()) {
            
            LocalDate hoje = LocalDate.now();
            LocalDate proximaExecucao = switch (taskAtual.getTipoRecorrencia()) {
                case DIARIA -> hoje.plusDays(1);
                case SEMANAL -> hoje.plusDays(7);
                case QUINZENAL -> hoje.plusDays(15);
                case MENSAL -> hoje.plusDays(30);
                default -> null;
            };
            taskAtual.setDataProximaExecucao(proximaExecucao);
        } else {
            // Se voltou a tarefa para pendente, cancela o agendamento
            taskAtual.setDataProximaExecucao(null);
        }

        // Salva a alteração da tarefa original (que agora foi para CONCLUIDA)
        return taskRepository.save(taskAtual);
    }

    // Robô que roda todo dia à meia-noite (00:00) para processar as recorrências
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void gerarTarefasRecorrentesAgendadas() {
        LocalDate hoje = LocalDate.now();
        List<Task> tarefasParaClonar = taskRepository.findByStatusAndDataProximaExecucaoLessThanEqual("CONCLUIDA", hoje);

        for (Task taskAnterior : tarefasParaClonar) {
            if (taskAnterior.getWorkspace().isConcluido()) continue;

            Task novaTarefa = new Task();
            novaTarefa.setTitulo(taskAnterior.getTitulo());
            novaTarefa.setDescricao(taskAnterior.getDescricao());
            novaTarefa.setWorkspace(taskAnterior.getWorkspace());
            novaTarefa.setStatus("PENDENTE");
            novaTarefa.setTipoRecorrencia(taskAnterior.getTipoRecorrencia());
            taskRepository.save(novaTarefa);

            // Limpa a antiga para não gerar clones duplicados e remove a tag visual "Recorrente" dela
            taskAnterior.setDataProximaExecucao(null);
            taskAnterior.setTipoRecorrencia(TipoRecorrencia.NENHUMA); 
            taskRepository.save(taskAnterior);

            notificacaoService.criarNotificacao(novaTarefa.getWorkspace().getUser(), "Sua tarefa recorrente retornou: " + novaTarefa.getTitulo());
        }
    }
}