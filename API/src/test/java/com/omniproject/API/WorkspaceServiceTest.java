package com.omniproject.api;

import com.omniproject.api.model.User;
import com.omniproject.api.model.Workspace;
import com.omniproject.api.repository.WorkspaceRepository;
import com.omniproject.api.service.ActivityLogService;
import com.omniproject.api.service.WorkspaceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito no JUnit 5
class WorkspaceServiceTest {

    // Mockamos as dependências que o Service precisa para funcionar
    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private ActivityLogService activityLogService;

    // Injeta os Mocks criados acima automaticamente na nossa classe alvo
    @InjectMocks
    private WorkspaceService workspaceService;

    private User usuarioDono;
    private Workspace workspaceFake;

    @BeforeEach
    void setUp() {
        usuarioDono = new User();
        usuarioDono.setId(1L);
        usuarioDono.setNome("Tech Lead");

        workspaceFake = new Workspace();
        workspaceFake.setId(100L);
        workspaceFake.setNome("Projeto Migração Cloud");
        workspaceFake.setDescricao("Refatorando para a camada de Service");
        workspaceFake.setUser(usuarioDono);
    }

    @Test
    @DisplayName("Deve criar um workspace com sucesso, vinculando o usuário e registrando log")
    void deveCriarWorkspaceComSucesso() {
        // Arrange: Ensinamos aos Mocks como eles devem se comportar
        when(workspaceRepository.save(any(Workspace.class))).thenReturn(workspaceFake);
        when(activityLogService.formatarAcaoCriacaoWorkspace(anyString(), anyString()))
                .thenReturn("Log simulado de criação");

        // Act: Executamos o método do Service (Assumindo a assinatura criarWorkspace(workspace, user))
        Workspace resultado = workspaceService.criarWorkspace(workspaceFake, usuarioDono);

        // Assert: Verificamos o resultado e se os Mocks foram chamados corretamente
        assertNotNull(resultado, "O workspace retornado não deveria ser nulo.");
        assertEquals("Projeto Migração Cloud", resultado.getNome());
        
        verify(workspaceRepository, times(1)).save(workspaceFake);
        verify(activityLogService, times(1)).registrarAcao(anyString(), eq(usuarioDono), eq(workspaceFake), isNull());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar buscar um workspace que não existe")
    void deveLancarExcecaoAoBuscarWorkspaceInexistente() {
        // Arrange: Simulamos o banco de dados não encontrando o ID
        Long idInvalido = 999L;
        when(workspaceRepository.findById(idInvalido)).thenReturn(Optional.empty());

        // Act & Assert: Validamos se a exceção correta é lançada ao chamar o método
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            workspaceService.buscarWorkspacePorId(idInvalido);
        });

        // Garantimos que o repositório foi de fato consultado
        verify(workspaceRepository, times(1)).findById(idInvalido);
        assertEquals("Projeto não encontrado.", exception.getMessage(), "A mensagem da exceção deve ser exata.");
    }
}