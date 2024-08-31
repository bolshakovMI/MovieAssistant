package com.example.movieAssistant.config;

import com.example.movieAssistant.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.sql.DataSource;
import static org.springframework.security.config.Customizer.withDefaults;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    DataSource dataSource;
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(
                authorize -> authorize
                    .requestMatchers("/auth/**").anonymous()

                    //user-info
                    .requestMatchers("/user-info/**").authenticated()

                    // users
                    .requestMatchers(HttpMethod.GET, "/users/all").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/users/new").anonymous()
                    .requestMatchers(HttpMethod.PUT, "/users/password").authenticated()
                    .requestMatchers(HttpMethod.GET, "/users/**").authenticated()
                    .requestMatchers("/users/**").hasRole("ADMIN")

                    // friendships
                    .requestMatchers(HttpMethod.GET, "/friendships/status/*").authenticated()
                    .requestMatchers(HttpMethod.GET, "/friendships/requests/incoming").authenticated()
                    .requestMatchers(HttpMethod.GET, "/friendships/requests/outgoing").authenticated()
                    .requestMatchers(HttpMethod.GET, "/friendships/friends").authenticated()
                    .requestMatchers(HttpMethod.GET, "/friendships/**").hasRole("ADMIN")
                    .requestMatchers("/friendships/**").authenticated()

                    // wish
                    .requestMatchers(HttpMethod.GET, "/wish/all").hasRole("ADMIN")
                    .requestMatchers("/wish/**").authenticated()

                    // swagger
                    .requestMatchers("/swagger-ui**").hasRole("ADMIN")
                    .requestMatchers("/v3/**").hasRole("ADMIN")

                    .anyRequest().permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(withDefaults())
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
