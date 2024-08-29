package com.example.movieAssistant.model.dto.response;

import com.example.movieAssistant.model.db.entity.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieCountResponse {

    MovieResponse movieResponse;
    Long count;

    public MovieCountResponse(Movie movie, Long count) {
        this.movieResponse = new MovieResponse(movie);
        this.count = count;
    }
}
