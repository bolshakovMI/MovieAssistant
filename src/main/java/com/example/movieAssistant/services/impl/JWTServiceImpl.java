package com.example.movieAssistant.services.impl;

import com.example.movieAssistant.exceptions.CustomException;
import com.example.movieAssistant.model.db.entity.User;
import com.example.movieAssistant.model.db.repository.UserRepo;
import com.example.movieAssistant.services.JWTService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class JWTServiceImpl implements JWTService {

    private final UserRepo repository;

    @Value("${jwt.signing_key}")
    public String jwtSigningKey;

    @Value("${jwt.expiration_time.access_token}")
    public long accessTokenExpirationTime;

    @Value("${jwt.expiration_time.refresh_token}")
    public long refreshTokenExpirationTime;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("scope", "access")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, jwtSigningKey)
                .compact();
    }

    public String generateRToken(String username) {
        String RToken = Jwts.builder()
                .setSubject(username)
                .claim("scope", "refresh")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, jwtSigningKey)
                .compact();

        Optional<User> userOptional = repository.findById(username);
        if(userOptional.isEmpty())
            throw new CustomException("Владелец токена отсутствует в БД", HttpStatus.NOT_FOUND);

        User user = userOptional.get();
        user.setToken(RToken);
        repository.save(user);

        return RToken;
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSigningKey).build()
                    .parseClaimsJws(token)
                    .getBody();

        }
        catch (ClaimJwtException e){
            return e.getClaims();
        }
        catch (JwtException e){
            throw new CustomException("invalid token", HttpStatus.BAD_REQUEST);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractScope(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("scope", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSigningKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isTokenExpiredButValid(String token) {
        try {
            Jwts.parser()
                .setSigningKey(jwtSigningKey).build()
                .parseClaimsJws(token)
                .getBody();
        } catch (SignatureException e) {
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
