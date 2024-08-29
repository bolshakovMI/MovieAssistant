package com.example.movieAssistant.model.db.repository;

import com.example.movieAssistant.model.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {}
