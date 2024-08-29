package com.example.movieAssistant.services;

import com.example.movieAssistant.exceptions.CustomException;
import com.example.movieAssistant.model.dto.request.LoginRequest;
import com.example.movieAssistant.model.dto.response.JwtAuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    JwtAuthenticationResponse signIn(LoginRequest request) throws CustomException;
    JwtAuthenticationResponse refreshToken(HttpServletRequest request) throws CustomException;
}
