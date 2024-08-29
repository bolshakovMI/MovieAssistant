package com.example.movieAssistant.model.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "token")
    String token;

    @Column(name = "enabled")
    boolean enabled;

    @Column(name = "created_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createdAt;

    @Column(name = "time_of_switch_status")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime timeOfSwitchStatus;

    @OneToOne(cascade = CascadeType.ALL, mappedBy="login", fetch = FetchType.EAGER)
    UserInfo userInfo;

    @OneToMany(mappedBy = "username", fetch = FetchType.EAGER)
    List<Authority> authorities = new ArrayList<>();

    public void addAuthority(Authority auth) {
        authorities.add(auth);
    }

    public List<String> getAuthoritiesString (){
        return authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public boolean isAdmin() {
        for (Authority authority: authorities){
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.enabled = true;
        this.createdAt = LocalDateTime.now();
    }
}
