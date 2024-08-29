package com.example.movieAssistant.model.db.repository;

import com.example.movieAssistant.model.db.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepo extends JpaRepository<Movie, Long> {}
