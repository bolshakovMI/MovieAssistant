package com.example.movieAssistant.security;

import com.example.movieAssistant.model.db.entity.User;
import com.example.movieAssistant.model.db.repository.UserRepo;
import com.example.movieAssistant.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepo repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
       FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring("Bearer ".length());

        String scope = jwtService.extractScope(jwt);
        if (!scope.equals("access")) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtService.extractUserName(jwt);

        if (username==null || username.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null){
            filterChain.doFilter(request, response);
            return;
        }

        if (!repository.existsById(username)) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = repository.findById(username).orElseThrow();
        user.getAuthorities();

        if (!jwtService.isTokenValid(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authToken);

        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }
}


