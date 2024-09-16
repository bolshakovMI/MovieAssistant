package com.example.movieAssistant.model.dto.response;

import com.example.movieAssistant.model.db.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class WishResponse {

    Long wishId;
    Long userId;
    boolean viewed;
    boolean deleted;
    List<String> tags;

    Long movieId;
    String name;
    short year;
    double rating;
    List<String> movieGenres;

    public WishResponse(Wish wish) {

        this.wishId = wish.getId();
        this.userId = wish.getUsername().getId();
        this.viewed = wish.isViewed();
        this.deleted = wish.isDeleted();
        this.tags = wish.getTagsString();

        Movie movie = wish.getMovie();

        this.movieId = movie.getId();
        this.name = movie.getName();
        this.year = movie.getYear();
        this.rating = movie.getRating();
        this.movieGenres = movie.getGenresToString();
    }
}
