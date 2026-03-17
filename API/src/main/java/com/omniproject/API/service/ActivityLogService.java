package com.omniproject.API.service;

import com.omniproject.API.model.ActivityLog;
import com.omniproject.API.model.Task;
import com.omniproject.API.model.User;
import com.omniproject.API.model.Workspace;
import com.omniproject.API.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public ActivityLog registrarAcao(String descricao, User usuario, Workspace workspace, Task task) {
        ActivityLog log = new ActivityLog();
        log.setDescricao(descricao);
        log.setUsuario(usuario);
        log.setWorkspace(workspace);
        log.setTask(task);
        return activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> buscarLogsPorWorkspace(Long workspaceId) {
        return activityLogRepository.findByWorkspaceIdOrderByDataHoraDesc(workspaceId);
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> buscarUltimosLogsPorWorkspace(Long workspaceId, int quantidade) {
        return activityLogRepository.findTop10ByWorkspaceIdOrderByDataHoraDesc(workspaceId);
    }

    public String formatarAcaoCriacaoTarefa(String nomeUsuario, String tituloTarefa) {
        return String.format("%s criou a tarefa '%s'", nomeUsuario, tituloTarefa);
    }

    public String formatarAcaoAtualizacaoStatus(String nomeUsuario, String tituloTarefa, String statusAnterior, String statusNovo) {
        return String.format("%s moveu a tarefa '%s' de %s para %s",
                nomeUsuario, tituloTarefa, statusAnterior, statusNovo);
    }

    public String formatarAcaoConclusaoWorkspace(String nomeUsuario, String nomeWorkspace, boolean concluido) {
        String acao = concluido ? "concluiu" : "reabriu";
        return String.format("%s %s o projeto '%s'", nomeUsuario, acao, nomeWorkspace);
    }

    public String formatarAcaoCriacaoWorkspace(String nomeUsuario, String nomeWorkspace) {
        return String.format("%s criou o projeto '%s'", nomeUsuario, nomeWorkspace);
    }

    public String formatarAcaoEdicaoWorkspace(String nomeUsuario, String nomeWorkspace) {
        return String.format("%s editou o projeto '%s'", nomeUsuario, nomeWorkspace);
    }

    public String formatarAcaoExclusaoTarefa(String nomeUsuario, String tituloTarefa) {
        return String.format("%s excluiu a tarefa '%s'", nomeUsuario, tituloTarefa);
    }

    public String formatarAcaoConvite(String nomeDono, String nomeConvidado, String nomeWorkspace) {
        return String.format("%s convidou %s para o projeto '%s'", nomeDono, nomeConvidado, nomeWorkspace);
    }
}
