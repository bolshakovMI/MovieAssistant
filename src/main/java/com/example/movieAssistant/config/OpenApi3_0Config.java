package com.example.movieAssistant.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Серверная часть приложения для хранения записей" +
        " о желании посмотреть фильмы или сериалы", version = "0.1"))
public class OpenApi3_0Config {
}
