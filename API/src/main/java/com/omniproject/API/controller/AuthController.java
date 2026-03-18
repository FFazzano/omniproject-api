package com.omniproject.API.controller;

import com.omniproject.API.config.TokenService;
import com.omniproject.API.model.User;
import com.omniproject.API.model.UserRole;
import com.omniproject.API.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    // 1. Injeção por Construtor (Padrão Sênior)
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserRepository repository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO data) {
        // Força o log aparecer imediatamente no painel do Render
        System.out.println("Recebendo login para: " + data.getEmail());

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getSenha());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.gerarToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new TokenDTO(token));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno detalhado: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO data) {
        if (this.repository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        String encryptedPassword = passwordEncoder.encode(data.senha());

        User newUser = new User();
        newUser.setNome(data.nome());
        newUser.setEmail(data.email());
        newUser.setSenha(encryptedPassword);
        newUser.setRole(UserRole.USER);

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    // ==========================================
    // DTOs (Data Transfer Objects) COM BLINDAGEM
    // ==========================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        private String email;

        @NotBlank(message = "A senha é obrigatória.")
        private String senha;
    }

    public record RegisterDTO(
            @NotBlank(message = "O nome é obrigatório.")
            String nome,

            @NotBlank(message = "O e-mail é obrigatório.")
            @Email(message = "Formato de e-mail inválido.")
            String email,

            @NotBlank(message = "A senha é obrigatória.")
            @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
            String senha
    ) {}

    public record TokenDTO(String token) {}
}