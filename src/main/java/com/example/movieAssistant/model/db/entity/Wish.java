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
@Table(name="wishes")
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=true)
    UserInfo username;

    @ManyToOne
    @JoinColumn(name="movie_id", nullable=true)
    Movie movie;

    @Column(name="viewed")
    boolean viewed;

    @Column(name="deleted")
    boolean deleted;

    @OneToMany(mappedBy = "wish", fetch = FetchType.EAGER)
    List<Tag> tags = new ArrayList<>();

    @Column(name = "created_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime updatedAt;

    public List<String> getTagsString (){
        return tags
                .stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
    }

    public Wish(UserInfo username, Movie movie) {
        this.username = username;
        this.movie = movie;
        this.viewed = false;
        this.deleted = false;
        this.createdAt = LocalDateTime.now();
    }
}
