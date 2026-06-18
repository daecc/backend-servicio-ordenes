package com.unmsm.marketplace.ordenes_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Microservicio de Ordenes - Marketplace")
                .version("1.0.0")
                .description("API para gestion de ordenes maestras, subordenes por vendedor y asignacion Round-Robin de sellers")
                .contact(new Contact()
                    .name("Grupo Ordenes")));
    }
}
