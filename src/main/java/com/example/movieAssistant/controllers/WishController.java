package com.example.movieAssistant.controllers;

import com.example.movieAssistant.model.dto.request.*;
import com.example.movieAssistant.model.dto.response.MovieCountResponse;
import com.example.movieAssistant.model.dto.response.MovieResponse;
import com.example.movieAssistant.model.dto.response.WishResponse;
import com.example.movieAssistant.services.WishParserService;
import com.example.movieAssistant.services.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/wish")
@RequiredArgsConstructor
@Tag(name="Записи о желании посмотреть фильм")
public class WishController {

    public final WishService wishService;
    public final WishParserService wishParserService;

    @PostMapping("/new")
    @Operation(summary = "Создание записи о желании посмотреть фильм")
    public WishResponse createWish(@RequestBody @Valid WishRequest request){
        return wishService.createWish(request);
    }

    @PostMapping("/new/pars")
    @Operation(summary = "Создание записи о желании посмотреть фильм по id сайта imdb.com")
    public WishResponse createWish(@RequestBody @Valid WishParseRequest request){
        return wishParserService.createWishWithParsing(request);
    }

    @PutMapping("/{wish-id}")
    @Operation(summary = "Изменение записи о желании посмотреть фильм")
    public WishResponse updateWish(@RequestBody @Valid WishUpdateRequest request, @PathVariable("wish-id") Long wishId){
        return wishService.updateWish(request, wishId);
    }

    @DeleteMapping("/{wish-id}")
    @Operation(summary = "Удаление записи о желании посмотреть фильм")
    public String deleteWish(@PathVariable("wish-id") Long wishId){
        return wishService.deleteWish(wishId);
    }

    @GetMapping("/{wish-id}")
    @Operation(summary = "Получение конкретной записи по id")
    public WishResponse getWish(@PathVariable("wish-id") Long wishId) {
        return wishService.getWish(wishId);
    }

    @GetMapping("/all")
    @Operation(summary = "Получение общего списка записей")
    public Page<WishResponse> getAllWishes(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer perPage,
                                              @RequestParam(defaultValue = "id") String sort,
                                              @RequestParam(defaultValue = "ASC") Sort.Direction order)
    {
        return wishService.getAllWishes(page, perPage, sort, order);
    }

    @GetMapping("/top")
    @Operation(summary = "Получение списка самых популярных фильмов в приложении")
    public List<MovieCountResponse> getTop(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer perPage) {
        return wishService.getTop(page, perPage);
    }

    @GetMapping("/users/any")
    @Operation(summary = "Получение случайного фильма из списка желаний")
    public WishResponse getMyRandomWish() {
        return wishService.getMyRandomWish();
    }

    @GetMapping("/users/any/param")
    @Operation(summary = "Получение случайного фильма из списка желаний с учетом переданных параметров")
    public WishResponse getMyRandomWishWithParam(@RequestBody @Valid WishParamRequest request) {
        return wishService.getMyRandomWishWithParam(request);
    }

    @GetMapping("/users/all")
    @Operation(summary = "Получение страницы фильмов из списка желаний")
    public Page<WishResponse> getPageMyWish(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer perPage,
                                            @RequestParam(defaultValue = "id") String sort,
                                            @RequestParam(defaultValue = "ASC") Sort.Direction order) {
        return wishService.getPageofMyWishes(page, perPage, sort, order);
    }

    @GetMapping("/users/all/param")
    @Operation(summary = "Получение страницы фильмов из списка желаний с учетом переданных параметров")
    public Page<WishResponse> getPageMyWishWithParam(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer perPage,
                                            @RequestBody @Valid WishParamRequest request) {
        return wishService.getPageofMyWishesWithParam(page, perPage, request);
    }


    @GetMapping("/shared/any")
    @Operation(summary = "Получение случайного фильма, общего для нескольких пользователей")
    public MovieResponse getOursRandomWish(@RequestBody @Valid WishWithFriendsRequest request) {
        return wishService.getOursRandomWish(request);
    }

    @GetMapping("/shared/any/param")
    @Operation(summary = "Получение случайного фильма, общего для нескольких пользователей " +
            "с учетом переданных параметров")
    public MovieResponse getOursRandomWishWithParam(@RequestBody @Valid WishParamWithFriendsRequest request) {
        return wishService.getOursRandomWishWithParam(request);
    }

    @GetMapping("/shared/all")
    @Operation(summary = "Получение списка фильмов, общих для нескольких пользователей")
    public Page<MovieResponse> getPageOursWish(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer perPage,
                                               @RequestBody @Valid WishWithFriendsRequest request) {
        return wishService.getPageofOursWishes(page, perPage, request);
    }

    @GetMapping("/shared/all/param")
    @Operation(summary = "Получение списка фильмов, общих для нескольких пользователей " +
            "с учетом переданных параметров")
    public Page<MovieResponse> getPageOursWish(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer perPage,
                                               @RequestBody @Valid WishParamWithFriendsRequest request) {
        return wishService.getPageofOursWishesWithParam(page, perPage, request);
    }
}
