package com.example.movieAssistant.model.dto.request;

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
public class WishUpdateRequest {
    boolean viewed;
    boolean shouldTagsBeChanged;
    List<String> tagNames;
}
