package com.omniproject.API.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Se você estava usando .allowedOriginPatterns("*"), localhost já deve funcionar.
                // Mas a prática Sênior e mais segura é listar as origens exatas:
                .allowedOrigins(
                        "http://localhost:5173", // Ambiente de Desenvolvimento (Vite)
                        "https://seu-frontend.vercel.app" // ATENÇÃO: Coloque aqui a URL real do seu Vercel
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true); // Necessário para enviar o token JWT/Cookies com segurança
    }
}
