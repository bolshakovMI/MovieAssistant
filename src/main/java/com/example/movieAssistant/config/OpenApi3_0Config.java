package com.example.movieAssistant.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Movie Assistant",
        description = "Серверная часть приложения для хранения записей о желании посмотреть фильмы или сериалы",
        version = "0.1"),
    security = {
        @SecurityRequirement(
            name = "Bearer Token"
        )
    }
)
@SecurityScheme(
    name="Bearer Token",
    description="Введите access JWT-token",
    scheme="bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApi3_0Config {
}
