package com.example.movieAssistant.model.dto.response;

import com.example.movieAssistant.model.db.entity.Authority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    String username;
    boolean enabled;
    List<Authority> authorities = new ArrayList<Authority>();
    UserInfoResponse userInfo;
}
