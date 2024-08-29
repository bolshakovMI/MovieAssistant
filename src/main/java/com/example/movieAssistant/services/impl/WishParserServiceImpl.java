package com.example.movieAssistant.services.impl;

import com.example.movieAssistant.exceptions.CustomException;
import com.example.movieAssistant.model.db.entity.Movie;
import com.example.movieAssistant.model.db.entity.Tag;
import com.example.movieAssistant.model.db.entity.UserInfo;
import com.example.movieAssistant.model.db.entity.Wish;
import com.example.movieAssistant.model.db.repository.GenreRepo;
import com.example.movieAssistant.model.db.repository.MovieRepo;
import com.example.movieAssistant.model.db.repository.TagRepo;
import com.example.movieAssistant.model.db.repository.WishRepo;
import com.example.movieAssistant.model.dto.request.WishParseRequest;
import com.example.movieAssistant.model.dto.request.WishRequest;
import com.example.movieAssistant.model.dto.response.WishResponse;
import com.example.movieAssistant.services.FriendService;
import com.example.movieAssistant.services.MovieService;
import com.example.movieAssistant.services.UserService;
import com.example.movieAssistant.services.WishParserService;
import com.example.movieAssistant.utils.ParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WishParserServiceImpl extends WishServiceImpl implements WishParserService {

    @Autowired
    public WishParserServiceImpl(UserService userService, FriendService friendService, MovieService movieService, MovieRepo movieRepo, GenreRepo genreRepo, WishRepo wishRepo, TagRepo tagRepo) {
        super(userService, friendService, movieService, movieRepo, wishRepo, tagRepo);
    }

    @Override
    public WishResponse createWishWithParsing(WishParseRequest request) {
        UserInfo user = userService.getThisUser().getUserInfo();

        long imdbIdLong = Long.parseLong(request.getImdbId());

        Movie movie;
        if (movieService.isMovieWithThisIdExists(imdbIdLong)) {
            movie = movieService.getMovieWithoutCheck(imdbIdLong);
        } else {
            WishRequest wishRequest;

            wishRequest = ParserUtil.getWishRequestByImdbId(request.getImdbId(), imdbIdLong);

            movie = movieService.createMovie(wishRequest);
        }

        if (wishRepo.existsWishByUsernameAndMovie(user, movie)) {
            throw new CustomException("Этот фильм уже добавлен пользователем в планируемые к просмотру", HttpStatus.BAD_REQUEST);
        }

        Wish wish = new Wish(user, movie);

        wish = wishRepo.save(wish);

        List<String> listString = request.getTagNames();

        if (listString!=null) {

            for (String str : listString) {
                Tag tag = new Tag(str);
                tag.setWish(wish);
                wish.getTags().add(tag);
                tagRepo.save(tag);
            }
        }

        return new WishResponse(wish);
    }
}
