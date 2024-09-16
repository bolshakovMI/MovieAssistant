package com.example.movieAssistant.services.impl;

import com.example.movieAssistant.exceptions.CustomException;
import com.example.movieAssistant.model.db.entity.Genre;
import com.example.movieAssistant.model.db.entity.Movie;
import com.example.movieAssistant.model.db.repository.GenreRepo;
import com.example.movieAssistant.model.db.repository.MovieRepo;
import com.example.movieAssistant.model.dto.request.WishRequest;
import com.example.movieAssistant.services.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepo movieRepo;
    private final GenreRepo genreRepo;

    @Override
    public Movie getMovieWithoutCheck(Long imdbId) {
        return movieRepo.findById(imdbId).get();
    }

    @Override
    public List<Movie> findSharedMoviesByUsers(List<Long> movies) {
        return movieRepo.findAllById(movies);
    }

    @Override
    public boolean isMovieWithThisIdExists(Long imdbId) {
        Optional<Movie> optionalMovie = movieRepo.findById(imdbId);
        return optionalMovie.isPresent();
    }

    @Override
    public Movie createOrReturnMovie(WishRequest request) {
        Movie movie = null;
        Optional<Movie> optionalMovie = movieRepo.findById(request.getImdbId());
        if (optionalMovie.isEmpty()) {

            movie = new Movie(request.getImdbId(), request.getName(), request.getYear(),
                    request.getRating());

            movie = movieRepo.save(movie);

            List<String> listString = request.getGenreNames();
            for (String str : listString) {
                Genre genre = new Genre(str);
                genre.setMovie(movie);
                movie.getGenres().add(genre);
                genreRepo.save(genre);
            }

        } else {
            movie = optionalMovie.get();
        }
        return movie;
    }

    @Override
    public Movie createMovie(WishRequest request) {
        Movie movie = new Movie(request.getImdbId(), request.getName(), request.getYear(), request.getRating());

        movie = movieRepo.save(movie);

        List<String> listString = request.getGenreNames();
        for (String str : listString) {
            Genre genre = new Genre(str);
            genre.setMovie(movie);
            movie.getGenres().add(genre);
            genreRepo.save(genre);
        }

        return movie;
    }

    @Override
    public String getMovieById(Long id){
        Movie movie = movieRepo.findMovieById(id).orElseThrow(
                () -> new CustomException("Фильм с указанным id не найден", HttpStatus.NOT_FOUND));
        return movie.getName();
    }

}
