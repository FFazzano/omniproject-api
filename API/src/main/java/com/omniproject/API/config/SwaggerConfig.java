package com.omniproject.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // --- TOQUE SÊNIOR: Identidade Visual da sua API no Swagger ---
                .info(new Info()
                        .title("OmniProject API")
                        .description("API RESTful para o sistema de gerenciamento de Projetos e Tarefas (SaaS).")
                        .version("v1.0.0"))

                // --- SEGURANÇA: Configuração do JWT (A sua lógica perfeita mantida) ---
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}