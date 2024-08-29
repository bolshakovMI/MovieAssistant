package com.example.movieAssistant.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoRequest {
    @NotEmpty(message = "Email must be set")
    @Email(message = "Email should be valid")
    String email;

    @Pattern(regexp = "^\\D*$", message = "Имя не должно содержать цифр")
    String firstName;
    @Pattern(regexp = "^\\D*$", message = "Фамилия не должна содержать цифр")
    String lastName;

    String birthDay;
}
