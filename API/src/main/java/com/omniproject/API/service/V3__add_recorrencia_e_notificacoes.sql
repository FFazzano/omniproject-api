-- Adicionando campos de recorrência na tabela de Tarefas
ALTER TABLE tasks ADD COLUMN data_vencimento DATE;
ALTER TABLE tasks ADD COLUMN tipo_recorrencia VARCHAR(50) DEFAULT 'NENHUMA';

-- Criando tabela de notificações
CREATE TABLE notificacoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    mensagem VARCHAR(255) NOT NULL,
    lida BOOLEAN DEFAULT FALSE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacao_usuario FOREIGN KEY (usuario_id) REFERENCES users(id) ON DELETE CASCADE
);