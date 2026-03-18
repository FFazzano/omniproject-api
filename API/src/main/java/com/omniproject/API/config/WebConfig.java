package com.omniproject.API.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Libera todas as rotas da API
                .allowedOrigins(
                        "http://localhost:5173", // URL do seu React local (Vite)
                        "https://seu-projeto.vercel.app" // Substitua pela URL real do seu front-end na Vercel
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                .allowedHeaders("*") // Permite todos os cabeçalhos (incluindo o Authorization)
                .allowCredentials(true); // Necessário se houver tráfego de cookies ou headers de autenticação complexos
    }
}