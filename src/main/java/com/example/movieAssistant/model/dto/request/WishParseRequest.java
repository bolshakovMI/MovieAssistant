package com.example.movieAssistant.model.dto.request;

import jakarta.validation.constraints.Pattern;
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
public class WishParseRequest {
    @Pattern(regexp = "^[0-9]{7,8}$", message = "ID фильма должен быть 7 или 8 значным числом")
    String imdbId;

    List<String> tagNames;
}
