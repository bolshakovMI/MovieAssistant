package com.example.movieAssistant.model.dto.request;

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
public class WishParamRequest {

    {
        this.yearFrom = 1895;
        this.yearTo = 2049;
        this.ratingFrom = 1.0;
        this.ratingTo = 10.0;
    }

    boolean viewed;

    @Digits(integer = 4, fraction = 0, message = "Минимальный год выпуска фильма необходимо указать в формате: XXXX")
    @Max(value = 2049, message = "Минимальный год выпуска фильма не может быть больше 2049")
    short yearFrom;

    @Digits(integer = 4, fraction = 0, message = "Максимальный год выпуска фильма необходимо указать в формате: XXXX")
    @Min(value = 1895, message = "Максимальный год выпуска фильма не может быть меньше 1895")
    short yearTo;

    @Max(value = 10, message = "Максимальный рейтинг фильма не может быть больше 10.0")
    double ratingFrom;

    @Min(value = 1, message = "Минимальный рейтинг фильма не может быть меньше 1.0")
    double ratingTo;

    List<String> genreNames;

    List<String> tagNames;
}
