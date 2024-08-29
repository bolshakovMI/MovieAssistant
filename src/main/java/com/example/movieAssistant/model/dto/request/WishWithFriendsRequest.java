package com.example.movieAssistant.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
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
public class WishWithFriendsRequest {
    @NotEmpty(message = "Необходимо указать друзей, с которыми вы хотите найти общий фильм")
    List<Long> friendsIds;
}
