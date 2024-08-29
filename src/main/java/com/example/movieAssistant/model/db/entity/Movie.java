package com.example.movieAssistant.model.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name="movies")
public class Movie {
    @Id
    @Column(name="imdb_id")
    Long id;

    @Column(name="name")
    String name;

    @Column(name="year")
    short year;

    @Column(name="rating")
    double rating;

    @Column(name = "created_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "movie", fetch = FetchType.EAGER)
    List<Genre> genres = new ArrayList<Genre>();

    public List<String> getGenresToString (){
        return genres
                .stream()
                .map(Genre::getGenreName)
                .collect(Collectors.toList());
    }

    public Movie(Long id, String name, short year, double rating) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }

}
