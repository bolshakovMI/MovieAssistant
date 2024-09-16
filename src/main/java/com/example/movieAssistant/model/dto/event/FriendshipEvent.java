package com.example.movieAssistant.model.dto.event;

import com.example.movieAssistant.model.enums.RequestConfirmationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendshipEvent {
    Long user1Id;
    String user1Login;
    List<String> user1friendsLogins;

    Long user2Id;
    String user2Login;
    List<String> user2friendsLogins;

    boolean isWithdrawn;
    RequestConfirmationStatus status;
}
