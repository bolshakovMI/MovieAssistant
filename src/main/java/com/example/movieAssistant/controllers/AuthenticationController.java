package com.example.movieAssistant.controllers;

import com.example.movieAssistant.model.dto.request.LoginRequest;
import com.example.movieAssistant.model.dto.response.JwtAuthenticationResponse;
import com.example.movieAssistant.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    AuthenticationService authService;

    @PostMapping("/login")
    @Operation(summary = "Вход пользователя в систему")
    public JwtAuthenticationResponse login(@RequestBody LoginRequest request){
        return authService.signIn(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Получение нового access токена")
    public JwtAuthenticationResponse refresh(HttpServletRequest request){
        return authService.refreshToken(request);
    }
}
