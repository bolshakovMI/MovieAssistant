package com.example.movieAssistant.model.dto.response;

import com.example.movieAssistant.model.db.entity.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieResponse {

    Long id;
    String name;
    short year;
    double rating;
    List<String> genres;

    public MovieResponse(Movie movie) {
        this.id = movie.getId();
        this.name = movie.getName();
        this.year = movie.getYear();
        this.rating = movie.getRating();
        this.genres = movie.getGenresToString();
    }
}
