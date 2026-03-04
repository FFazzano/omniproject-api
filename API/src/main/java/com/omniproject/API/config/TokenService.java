package com.omniproject.API.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.omniproject.API.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Essa é a "senha mestra" da sua API. Na vida real, isso fica escondido em variáveis de ambiente!
    @Value("${api.security.token.secret:meu-segredo-super-secreto-omniproject-123}")
    private String secret;

    public String gerarToken(User user) {
        try {
            // Escolhemos o algoritmo de criptografia e passamos o nosso segredo
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("OmniProject API") // Quem está emitindo o token
                    .withSubject(user.getEmail())  // Quem é o dono do token (vamos usar o e-mail)
                    .withExpiresAt(gerarDataExpiracao()) // O token tem validade (ex: 2 horas)
                    .sign(algorithm); // Assina e gera a String do token
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("OmniProject API")
                    .build()
                    .verify(token) // Verifica se o token é válido, não expirou e se o segredo bate
                    .getSubject(); // Devolve o e-mail do usuário logado
        } catch (JWTVerificationException exception){
            // Se o token for inválido, falso ou expirado, devolve vazio e o Spring bloqueia a requisição
            return "";
        }
    }

    private Instant gerarDataExpiracao() {
        // O token expira em 2 horas. Assim, se alguém roubar o token, ele não dura para sempre.
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}