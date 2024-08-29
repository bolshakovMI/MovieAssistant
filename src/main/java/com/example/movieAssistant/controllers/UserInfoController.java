package com.example.movieAssistant.controllers;

import com.example.movieAssistant.model.dto.request.UserInfoRequest;
import com.example.movieAssistant.model.dto.response.UserInfoResponse;
import com.example.movieAssistant.services.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-info")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/all")
    @Operation(summary = "Получение информации обо всех пользователях")
    public Page<UserInfoResponse> getAllUsers(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer perPage,
                                              @RequestParam(defaultValue = "login") String sort,
                                              @RequestParam(defaultValue = "ASC") Sort.Direction order)
    {
        return userInfoService.getAllUsers(page, perPage, sort, order);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение информации о конкретном пользователе")
    public UserInfoResponse getUser(@PathVariable Long id) {
        return userInfoService.getUser(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Редактирование информации о пользователе")
    public UserInfoResponse updateUser(@PathVariable Long id, @RequestBody @Valid UserInfoRequest request) {
        return userInfoService.updateUser(id, request);
    }
}
