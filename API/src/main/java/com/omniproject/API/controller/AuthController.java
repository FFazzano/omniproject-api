package com.omniproject.API.controller;

import com.omniproject.API.config.TokenService;
import com.omniproject.API.model.User;
import com.omniproject.API.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Se a senha estiver certa, gera a "Pulseira VIP"
        var token = tokenService.gerarToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO data) {
        // Verifica se o e-mail já existe no banco
        if (this.repository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        // Criptografa a senha antes de salvar (NUNCA salvar senha em texto puro!)
        String encryptedPassword = passwordEncoder.encode(data.senha());

        User newUser = new User();
        newUser.setNome(data.nome());
        newUser.setEmail(data.email());
        newUser.setSenha(encryptedPassword);

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    // --- DTOs (Data Transfer Objects) ---
    // Usamos 'record' do Java para criar pacotes de dados leves e seguros
    public record LoginDTO(String email, String senha) {}
    public record RegisterDTO(String nome, String email, String senha) {}
    public record TokenDTO(String token) {}
}