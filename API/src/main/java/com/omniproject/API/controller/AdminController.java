package com.omniproject.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/painel")
    public ResponseEntity<String> acessarPainel() {
        return ResponseEntity.ok("Bem-vindo, Chefe! Você tem acesso total ao sistema OmniProject.");
    }
}