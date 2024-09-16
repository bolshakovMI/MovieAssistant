package com.example.movieAssistant.services;

import com.example.movieAssistant.model.db.entity.Movie;
import com.example.movieAssistant.model.dto.request.WishRequest;

import java.util.List;

public interface MovieService {
    Movie getMovieWithoutCheck(Long imdbId);

    List<Movie> findSharedMoviesByUsers(List<Long> movies);

    boolean isMovieWithThisIdExists(Long imdbId);

    Movie createOrReturnMovie(WishRequest request);

    Movie createMovie(WishRequest request);

    String getMovieById(Long id);
}
