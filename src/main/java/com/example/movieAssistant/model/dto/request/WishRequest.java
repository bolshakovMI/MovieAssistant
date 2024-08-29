package com.example.movieAssistant.model.dto.request;

import com.example.movieAssistant.anotations.ImdbId;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WishRequest {
    @ImdbId(message = "ID фильма должен быть 7 или 8 значным числом")
    Long imdbId;

    @NotEmpty(message = "Необходимо указать имя фильма")
    String name;

    @NotNull(message = "Необходимо указать год выпуска фильма")
    @Digits(integer = 4, fraction = 0, message = "Год выпуска фильма необходимо указать в формате: XXXX")
    @Max(value = 2049, message = "Год выпуска фильма не может быть больше 2049")
    @Min(value = 1895, message = "Год выпуска фильма не может быть меньше 1895")
    Short year;

    @NotNull(message = "Необходимо указать рейтинг фильма")
    @Max(value = 10, message = "Рейтинг фильма не может быть больше 10.0")
    @Min(value = 1, message = "Рейтинг фильма не может быть меньше 1.0")
    Double rating;

    @NotEmpty(message = "Необходимо указать жанры фильма")
    List<String> genreNames;

    List<String> tagNames;

    public WishRequest(Long imdbId, String name, Short year, Double rating, List<String> genreNames) {
        this.imdbId = imdbId;
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.genreNames = genreNames;
    }
}
