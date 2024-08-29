package com.example.movieAssistant.model.db.repository;

import com.example.movieAssistant.model.db.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepo extends JpaRepository<Genre, Integer> {}
