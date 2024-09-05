package com.example.movieAssistant.controllers;

import com.example.movieAssistant.model.dto.request.AuthorityRequest;
import com.example.movieAssistant.model.dto.request.PasswordChangeRequest;
import com.example.movieAssistant.model.dto.request.UserRequest;
import com.example.movieAssistant.model.dto.response.AuthorityResponse;
import com.example.movieAssistant.model.dto.response.JwtAuthenticationResponse;
import com.example.movieAssistant.model.dto.response.UserResponse;
import com.example.movieAssistant.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Tag(name="Аккаунты пользователей")
public class UserController {

    UserService userService;

    @GetMapping("/all")
    @Operation(summary = "Получение учетных данных обо всех аккаунтах")
    public Page<UserResponse>  getAllUsers(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer perPage,
                                           @RequestParam(defaultValue = "username") String sort,
                                           @RequestParam(defaultValue = "ASC") Sort.Direction order){
            return userService.getAllUsers(page, perPage, sort, order);
    }

    @GetMapping("/{user}")
    @Operation(summary = "Получение учетных данных одного аккаунта")
    public UserResponse getUserResponse(@PathVariable String user){
            return userService.getUserResponse(user);
        }

    @PostMapping("/new")
    @Operation(summary = "Регистрация нового пользователя")
    public JwtAuthenticationResponse createUser(@RequestBody @Valid UserRequest request){
        return userService.createUser(request);
    }

    @PutMapping("/password")
    @Operation(summary = "Изменение пароля пользователя")
    public String updatePassword(@RequestBody @Valid PasswordChangeRequest request){
            return userService.updatePassword(request);
    }

    @PutMapping("/authority/set")
    @Operation(summary = "Изменение полномочий пользователя")
    public AuthorityResponse updateAuthority(@RequestBody AuthorityRequest request){
        return userService.updateAuthority(request);
    }

    @PutMapping("/authority/add")
    @Operation(summary = "Добавление полномочий пользователя")
    public AuthorityResponse addAuthority(@RequestBody AuthorityRequest request){
        return userService.addAuthority(request.getUsername(), request.getAuthorities());
    }

    @PutMapping("/authority/delete")
    @Operation(summary = "Отзыв полномочий пользователя")
    public AuthorityResponse deleteAuthority(@RequestBody AuthorityRequest request){
        return userService.deleteAuthority(request.getUsername(), request.getAuthorities());
    }

    @PutMapping("/{username}/{is-enable}")
    @Operation(summary = "Блокировка/разблокировка пользователя")
    public String setEnable(@PathVariable String username, @PathVariable("is-enable") boolean isEnable){
        return userService.setEnable(username, isEnable);
    }
}




