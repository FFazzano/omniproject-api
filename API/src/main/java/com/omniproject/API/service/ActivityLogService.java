package com.omniproject.api.service;

import com.omniproject.api.model.User;
import com.omniproject.api.model.Workspace;
import com.omniproject.api.model.Task;
import com.omniproject.api.model.ActivityLog;
import com.omniproject.api.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

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
        ActivityLog log = new ActivityLog();
        log.setDescricao(acao);
        log.setWorkspace(workspace);
        log.setDataHora(LocalDateTime.now());
        
        // Associar a tarefa se o log for específico de uma tarefa
        if (detalhes instanceof Task) {
            log.setTask((Task) detalhes);
        }
        
        activityLogRepository.save(log);
    }

    // Retorna a lista de logs de um workspace específico
    public List<ActivityLog> buscarLogsPorWorkspace(Long workspaceId) {
        List<ActivityLog> logs = activityLogRepository.findByWorkspaceId(workspaceId);
        // Ordena para que os mais recentes apareçam no topo
        logs.sort(Comparator.comparing(ActivityLog::getDataHora).reversed());
        return logs;
    }

    // Retorna os últimos N logs de um workspace específico
    public List<ActivityLog> buscarUltimosLogsPorWorkspace(Long workspaceId, int limit) {
        return buscarLogsPorWorkspace(workspaceId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}