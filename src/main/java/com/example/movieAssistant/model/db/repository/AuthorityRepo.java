package com.example.movieAssistant.model.db.repository;

import com.example.movieAssistant.model.db.entity.Authority;
import com.example.movieAssistant.model.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepo extends JpaRepository<Authority, Long> {
    void deleteAllByUsername(User username);
}
