package com.omniproject.api;

import com.omniproject.api.model.User;
import com.omniproject.api.service.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private User usuarioFake;

    @BeforeEach
    void setUp() {
        // Arrange Global: Instanciamos o TokenService manualmente com um "secret" de teste.
        // Evitar o @SpringBootTest aqui deixa a execução do teste absurdamente rápida.
        tokenService = new TokenService("segredo-super-seguro-para-testes-unitarios");

        // Preparamos o usuário fake que será usado em todos os testes
        usuarioFake = new User();
        usuarioFake.setId(1L);
        usuarioFake.setNome("QA Engineer");
        usuarioFake.setEmail("qa@omniproject.com");
        usuarioFake.setSenha("senhaForte123");
    }

    @Test
    @DisplayName("Deve gerar o token JWT com sucesso para um usuário válido")
    void deveGerarTokenComSucesso() {
        // Act: Executa o método que queremos testar
        String token = tokenService.gerarToken(usuarioFake);

        // Assert: Valida se o resultado é o esperado
        assertNotNull(token, "O token gerado não deveria ser nulo.");
        assertFalse(token.trim().isEmpty(), "O token gerado não deveria ser vazio.");
        
        // Bônus: Um JWT válido sempre começa com os dados do cabeçalho (que costumam virar "eyJ" em Base64)
        assertTrue(token.startsWith("eyJ"), "O token gerado não tem a estrutura padrão de um JWT.");
    }

    @Test
    @DisplayName("Deve validar o token gerado e extrair o email (subject) corretamente")
    void deveRecuperarSubjectComSucesso() {
        // Arrange: Precisamos gerar um token primeiro
        String token = tokenService.gerarToken(usuarioFake);

        // Act: Extrair o subject do token
        String subjectRetornado = tokenService.validarToken(token);

        // Assert: Garantir que o email que entrou é o mesmo que saiu
        assertNotNull(subjectRetornado, "O subject retornado não pode ser nulo.");
        assertEquals(usuarioFake.getEmail(), subjectRetornado, "O email extraído do token deve ser igual ao do usuário.");
    }

    @Test
    @DisplayName("Deve retornar uma string vazia ao tentar validar um token inválido ou adulterado")
    void deveFalharAoValidarTokenInvalido() {
        // Act: Tenta validar um token mal formatado/inválido
        String subjectRetornado = tokenService.validarToken("um.token.totalmente.invalido");

        // Assert: Conforme sua lógica no TokenService, deve cair no catch e retornar ""
        assertTrue(subjectRetornado.isEmpty(), "Deve retornar uma string vazia quando o token for inválido.");
    }
}