package com.example.movieAssistant.services;

import com.example.movieAssistant.model.db.entity.Movie;
import com.example.movieAssistant.model.db.entity.Wish;
import com.example.movieAssistant.model.dto.request.*;
import com.example.movieAssistant.model.dto.response.MovieCountResponse;
import com.example.movieAssistant.model.dto.response.MovieResponse;
import com.example.movieAssistant.model.dto.response.WishResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface WishService {
    WishResponse createWish(WishRequest request);
    WishResponse updateWish(WishUpdateRequest request, Long wishId);
    String deleteWish(Long wishId);
    Wish getWishDb(Long id);
    WishResponse getWish(Long wishId);
    Page<WishResponse> getAllWishes(Integer page, Integer perPage, String sort, Sort.Direction order);
    List<MovieCountResponse> getTop(Integer page,
                                    Integer perPage);

    // MY
    WishResponse getMyRandomWish();
    WishResponse getMyRandomWishWithParam(WishParamRequest request);
    Page<WishResponse> getPageofMyWishes(Integer page, Integer perPage, String sort, Sort.Direction order);
    Page<WishResponse> getPageofMyWishesWithParam(Integer page, Integer perPage, WishParamRequest request);

    // ALL
    MovieResponse getOursRandomWish(WishWithFriendsRequest request);
    MovieResponse getOursRandomWishWithParam(WishParamWithFriendsRequest request);
    Page<MovieResponse> getPageofOursWishes(Integer page, Integer perPage, WishWithFriendsRequest request);
    Page<MovieResponse> getPageofOursWishesWithParam(Integer page, Integer perPage, WishParamWithFriendsRequest request);

    // LIST
    List<Wish> getListByParam(WishParamRequest request);
    List<Movie> getSharedListByParam(WishParamWithFriendsRequest request);
}
