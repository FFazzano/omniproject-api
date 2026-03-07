package com.omniproject.API.controller;

import com.omniproject.API.config.TokenService;
import com.omniproject.API.model.User;
import com.omniproject.API.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.gerarToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(token));
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

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    // ==========================================
    // DTOs (Data Transfer Objects) COM BLINDAGEM
    // ==========================================

    public record LoginDTO(
            @NotBlank(message = "O e-mail é obrigatório.")
            @Email(message = "Formato de e-mail inválido.")
            String email,

            @NotBlank(message = "A senha é obrigatória.")
            String senha
    ) {}

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