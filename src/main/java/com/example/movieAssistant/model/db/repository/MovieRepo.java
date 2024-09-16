package com.example.movieAssistant.model.db.repository;

import com.example.movieAssistant.model.db.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MovieRepo extends JpaRepository<Movie, Long> {
    Optional<Movie> findMovieById(Long id);
}
