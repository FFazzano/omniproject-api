package com.omniproject.api.controller;

import com.omniproject.api.dto.TrixRequestDTO;
import com.omniproject.api.dto.TrixResponseDTO;
import com.omniproject.api.model.User;
import com.omniproject.api.service.TrixService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trix")
public class TrixController {

    private final TrixService trixService;

    public TrixController(TrixService trixService) {
        this.trixService = trixService;
    }

    @PostMapping
    public ResponseEntity<TrixResponseDTO> processarComando(
            @RequestBody TrixRequestDTO request,
            @AuthenticationPrincipal User usuarioLogado) {
        
        // Envia o texto da requisição e o usuário atrelado à requisição JWT
        TrixResponseDTO response = trixService.processarComandoTrix(request, usuarioLogado);
        return ResponseEntity.ok(response);
    }
}