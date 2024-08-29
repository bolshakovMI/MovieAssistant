package com.example.movieAssistant.model.dto.response;

import com.example.movieAssistant.model.enums.RequestConfirmationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendshipResponse {
    Long id;
    UserInfoResponse user1;
    UserInfoResponse user2;
    boolean isWithdrawn;
    RequestConfirmationStatus status;
}
