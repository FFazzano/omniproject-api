package com.omniproject.api.service;

import com.omniproject.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    // 1. Variável 'final' garante que ela não seja alterada depois de criada
    private final UserRepository repository;

    // 2. Injeção por Construtor (O padrão ouro do Spring Boot hoje em dia)
    public AuthorizationService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}