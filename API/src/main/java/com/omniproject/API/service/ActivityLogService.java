package com.omniproject.API.service;

import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.model.ActivityLog;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityLogService {

    // Retorna a string formatada para o log
    public String formatarAcaoCriacaoWorkspace(String workspaceNome, String userName) {
        return "Workspace " + workspaceNome + " criado por " + userName;
    }

    // Retorna a string formatada para a edição do workspace
    public String formatarAcaoEdicaoWorkspace(String workspaceNome, String userName) {
        return "Workspace " + workspaceNome + " editado por " + userName;
    }

    // Retorna a string formatada para a conclusão/reabertura do workspace
    public String formatarAcaoConclusaoWorkspace(String workspaceNome, String userName, boolean concluido) {
        return "Workspace " + workspaceNome + (concluido ? " concluído" : " reaberto") + " por " + userName;
    }

    // Retorna a string formatada para o envio de convite a um membro
    public String formatarAcaoConvite(String workspaceNome, String inviterName, String inviteeEmail) {
        return "Usuário " + inviteeEmail + " convidado para o Workspace " + workspaceNome + " por " + inviterName;
    }

    // Retorna a string formatada para a criação de uma tarefa
    public String formatarAcaoCriacaoTarefa(String taskTitle, String userName) {
        return "Tarefa " + taskTitle + " criada por " + userName;
    }

    // Retorna a string formatada para a atualização de status de uma tarefa
    public String formatarAcaoAtualizacaoStatus(String taskTitle, String oldStatus, String newStatus, String userName) {
        return "Status da tarefa " + taskTitle + " alterado de " + oldStatus + " para " + newStatus + " por " + userName;
    }

    // Retorna a string formatada para a exclusão de uma tarefa
    public String formatarAcaoExclusaoTarefa(String taskTitle, String userName) {
        return "Tarefa " + taskTitle + " excluída por " + userName;
    }

    // Assinatura do método que registra a ação no banco de dados (pode ser expandido depois)
    public void registrarAcao(String acao, User usuario, Workspace workspace, Object detalhes) {
        // Aqui irá a lógica de salvar na entidade ActivityLog (ex: activityLogRepository.save(...))
        System.out.println("LOG REGISTRADO: " + acao);
    }

    // Retorna a lista de logs de um workspace específico
    public List<ActivityLog> buscarLogsPorWorkspace(Long workspaceId) {
        // Aqui irá a lógica real, ex: return activityLogRepository.findByWorkspaceId(workspaceId);
        return new ArrayList<>(); // Retornando lista vazia apenas para compilar por enquanto
    }

    // Retorna os últimos N logs de um workspace específico
    public List<ActivityLog> buscarUltimosLogsPorWorkspace(Long workspaceId, int limit) {
        // Aqui irá a lógica real com paginação, ex: return activityLogRepository.findByWorkspaceIdOrderByDataHoraDesc(workspaceId, PageRequest.of(0, limit));
        return new ArrayList<>(); // Retornando lista vazia apenas para compilar por enquanto
    }
}