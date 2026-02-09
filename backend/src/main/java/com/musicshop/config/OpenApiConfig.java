package com.musicshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NTO Music API")
                        .version("1.0")
                        .description("REST API for NTO Music - an online music instrument store. " +
                                "Browse products, manage your cart, and complete purchases.")
                        .contact(new Contact()
                                .name("NTO Music")
                                .email("support@ntomusic.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server (direct backend)"),
                        new Server()
                                .url("http://localhost")
                                .description("Production server (via nginx) - requires docker compose")
                ));
    }
}
