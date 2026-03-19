package com.omniproject.api.service;

import com.omniproject.api.model.Notificacao;
import com.omniproject.api.model.User;
import com.omniproject.api.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public void criarNotificacao(User usuario, String mensagem) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacaoRepository.save(notificacao);
    }
}