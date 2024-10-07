package com.example.movieAssistant.services.impl;

import com.example.movieAssistant.exceptions.CustomException;
import com.example.movieAssistant.model.db.entity.*;
import com.example.movieAssistant.model.db.repository.*;
import com.example.movieAssistant.model.dto.request.*;
import com.example.movieAssistant.model.dto.response.MovieCountResponse;
import com.example.movieAssistant.model.dto.response.MovieResponse;
import com.example.movieAssistant.model.dto.response.WishResponse;
import com.example.movieAssistant.services.*;
import com.example.movieAssistant.utils.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Primary
@RequiredArgsConstructor
public class WishServiceImpl implements WishService {
    
    protected final UserService userService;
    protected final FriendService friendService;
    protected final MovieService movieService;
    protected final MovieRepo movieRepo;
    protected final WishRepo wishRepo;
    protected final TagRepo tagRepo;

    @Transactional
    @Override
    public WishResponse createWish(WishRequest request) {
        Movie movie = movieService.createOrReturnMovie(request);
        UserInfo user = userService.getThisUser().getUserInfo();

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

    @Transactional
    @Override
    public WishResponse updateWish(WishUpdateRequest request, Long wishId) {
        User user = userService.getThisUser();
        Wish wish = getWishDb(wishId);

        if(!wish.getUsername().equals(user.getUserInfo()) && !user.isAdmin()){
            throw new CustomException("Запись не может быть изменена данным пользователем", HttpStatus.FORBIDDEN);
        }

        wish.setViewed(request.isViewed());

        if(request.isShouldTagsBeChanged()) {
            tagRepo.deleteAllByWish(wish);
            tagRepo.flush();
            wish.setTags(new ArrayList<Tag>());

            if (request.getTagNames() != null) {
                for (String str : request.getTagNames()) {
                    Tag tag = new Tag(str);
                    tag.setWish(wish);
                    wish.getTags().add(tag);
                    tagRepo.save(tag);
                }
            }
        }

        wish.setUpdatedAt(LocalDateTime.now());
        wish = wishRepo.save(wish);

        return new WishResponse(wish);
    }

    @Override
    public String deleteWish(Long wishId) {
        Wish wish = getWishDb(wishId);
        User user = userService.getThisUser();

        if(!wish.getUsername().equals(user.getUserInfo()) && !user.isAdmin()){
            throw new CustomException("Запись не может быть изменена данным пользователем", HttpStatus.FORBIDDEN);
        }

        if (wish.isDeleted()) {
            return "Данная запись уже удалена";
        }
        wish.setDeleted(true);
        wish.setUpdatedAt(LocalDateTime.now());
        wishRepo.save(wish);
        return "Запись №" + wish.getId() + " о фильме " + wish.getMovie().getName() + " удалена";
    }

    @Override
    public Wish getWishDb(Long id) {
        return wishRepo.findById(id).orElseThrow(() -> new CustomException("Запись не найдена", HttpStatus.NOT_FOUND));
    }

    @Override
    public WishResponse getWish(Long wishId) {
        Wish wish = getWishDb(wishId);

        User user = userService.getThisUser();
        UserInfo userInfo = user.getUserInfo();
        UserInfo wishUser = wish.getUsername();

        if(wishUser.equals(userInfo) || friendService.isTheyAreFriends(userInfo, wishUser) || user.isAdmin()){
            return new WishResponse(wish);
        } else {
            throw new CustomException("У пользователя нет прав на чтение этой записи", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public Page<WishResponse> getAllWishes(Integer page, Integer perPage, String sort, Sort.Direction order) {
        Pageable request = PaginationUtil.getPageRequest(page, perPage, sort, order);
        List<WishResponse> all = wishRepo.findAll(request)
                .getContent()
                .stream()
                .map(WishResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }

    @Override
    public List<MovieCountResponse> getTop(Integer page, Integer perPage) {
        Pageable pageRequest = PaginationUtil.getPageRequest(page, perPage, "wishCount", Sort.Direction.DESC);

        List<Map<String, Object>>  listMap = wishRepo.findMostPopularMovies(pageRequest);
        return listMap.stream()
                .map(map->{
                    Long movieId = (Long) map.get("movieId");
                    Long wishCount = (Long) map.get("wishCount");
                    return new MovieCountResponse(movieRepo.findById(movieId).get(), wishCount);
                }).toList();
    }

    // MY
    @Override
    public WishResponse getMyRandomWish() {
        UserInfo user = userService.getThisUser().getUserInfo();

        Wish wish = wishRepo.findRandomWishByUsername(user)
                .orElseThrow(() -> new CustomException("У пользователя нет фильмов в списке запланированных к просмотру", HttpStatus.FORBIDDEN));

        return new WishResponse(wish);
    }

    @Override
    public WishResponse getMyRandomWishWithParam(WishParamRequest request) {
        List<Wish> listWish = getListByParam(request);

        if(listWish.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(listWish.size());

        return new WishResponse(listWish.get(randomIndex));
    }

    @Override
    public Page<WishResponse> getPageofMyWishes(Integer page, Integer perPage, String sort, Sort.Direction order) {
        UserInfo user = userService.getThisUser().getUserInfo();
        Pageable pageRequest = PaginationUtil.getPageRequest(page, perPage, sort, order);
        Page<Wish> wishes = wishRepo.findPageWishByUsername(user, pageRequest);

        List<WishResponse> all = wishes
                .getContent()
                .stream()
                .map(WishResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }

    @Override
    public Page <WishResponse> getPageofMyWishesWithParam(Integer page,
                                                          Integer perPage,
                                                          WishParamRequest request) {
        if(perPage<1){
            throw new CustomException("Количество записей на странице (переменная perPage) не может быть меньше 1", HttpStatus.BAD_REQUEST);
        }

        List<Wish> listWish = getListByParam(request);

        if (listWish.size()<(page-1)*perPage+1) {
            return new PageImpl<>(List.of());
        }
        List<Wish> subListWish;
        if (listWish.size()<page*perPage+1) {
            subListWish = listWish.subList((page-1)*perPage, listWish.size());
        } else {
            subListWish = listWish.subList((page-1)*perPage, page*perPage);
        }

        List<WishResponse> all = subListWish
                .stream()
                .map(WishResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }



    // OURS
    @Override
    public MovieResponse getOursRandomWish(WishWithFriendsRequest request) {
        UserInfo user = userService.getThisUser().getUserInfo();

        List<Long> listRequest = request.getFriendsIds();

        listRequest.forEach(user2 ->{
            if (!friendService.isTheyAreFriends(user.getId(), user2)){
                throw new CustomException("Один из пользователей в списке не состоит в друзьях" +
                        " текущего пользователя", HttpStatus.FORBIDDEN);
            }
        });

        listRequest.add(user.getId());

        List<Long> moviesIds = wishRepo.findSharedMoviesIdsByUsers(listRequest, listRequest.size());


        if (moviesIds.isEmpty()) {
            throw new CustomException("У пользователей нет общих фильмов", HttpStatus.BAD_REQUEST);
        }
        Random random = new Random();
        int randomIndex = random.nextInt(moviesIds.size());
        Movie randomMovie = movieRepo.findById(moviesIds.get(randomIndex)).get();

        return new MovieResponse(randomMovie);
    }

    @Override
    public MovieResponse getOursRandomWishWithParam(WishParamWithFriendsRequest request) {
        List<Movie> movies = getSharedListByParam(request);

        if (movies.isEmpty()) {
            throw new CustomException("У пользователей нет общих фильмов", HttpStatus.BAD_REQUEST);
        }

        Random random = new Random();
        int randomIndex = random.nextInt(movies.size());
        Movie randomMovie = movies.get(randomIndex);

        return new MovieResponse(randomMovie);
    }

    @Override
    public Page<MovieResponse> getPageofOursWishes(Integer page, Integer perPage, WishWithFriendsRequest request) {
        UserInfo user = userService.getThisUser().getUserInfo();

        List<Long> listRequest = request.getFriendsIds();

        listRequest.forEach(user2 ->{
            if (!friendService.isTheyAreFriends(user.getId(), user2)){
                throw new CustomException("Один из пользователей в списке не состоит в друзьях" +
                        " текущего пользователя", HttpStatus.FORBIDDEN);
            }
        });

        listRequest.add(user.getId());

        Pageable pageRequest = PaginationUtil.getPageRequest(page, perPage, "m.imdb_id", Sort.Direction.DESC);
        Page<Long> moviesIds = wishRepo.findPageSharedMoviesIdsByUsers(listRequest, listRequest.size(), pageRequest);
        List<Movie> moviesList = movieService.findSharedMoviesByUsers(moviesIds.toList());


        List<MovieResponse> all = moviesList
                .stream()
                .map(MovieResponse::new)
                .collect(Collectors.toList());
        return new PageImpl<>(all);
    }

    @Override
    public Page<MovieResponse> getPageofOursWishesWithParam(Integer page,
                                                            Integer perPage,
                                                            WishParamWithFriendsRequest request) {
        if(perPage<1){
            throw new CustomException("Количество записей на странице (переменная perPage) не может быть меньше 1", HttpStatus.BAD_REQUEST);
        }

        List<Movie> listMovie = getSharedListByParam(request);

        if (listMovie.size()<(page-1)*perPage+1) {
            return new PageImpl<>(List.of());
        }
        List<Movie> subListMovie;
        if (listMovie.size()<page*perPage+1) {
            subListMovie = listMovie.subList((page-1)*perPage, listMovie.size());
        } else {
            subListMovie = listMovie.subList((page-1)*perPage, page*perPage);
        }

        List<MovieResponse> all = subListMovie
                .stream()
                .map(MovieResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }



    // LIST
    @Override
    public List<Wish> getListByParam(WishParamRequest request) {
        UserInfo user = userService.getThisUser().getUserInfo();
        List <Wish> wishes = wishRepo.findListWishByUsernameWithParam(user, request.isViewed(),
                request.getYearFrom(), request.getYearTo(), request.getRatingFrom(), request.getRatingTo());

        if(wishes.isEmpty()){
            return new ArrayList<Wish>();
        }

        return filterWishByGenresAndTags(wishes, request.getGenreNames(), request.getTagNames());
    }

    private static List<Wish> filterWishByGenresAndTags(List<Wish> wishes, List<String> genreList, List<String> tagList) {
        Stream<Wish> streamWish = wishes.stream();

        if (genreList !=null) {
            streamWish = streamWish.filter(wish -> {
                for (String string: genreList) {
                    if (!wish.getMovie().getGenresToString().contains(string))
                        return false;
                }
                return true;
            });
        }

        if (tagList !=null) {
            streamWish = streamWish.filter(wish -> {
                for (String string: tagList) {
                    if (!wish.getTagsString().contains(string))
                        return false;
                }
                return true;
            });
        }
        return streamWish.collect(Collectors.toList());
    }

    @Override
    public List<Movie> getSharedListByParam(WishParamWithFriendsRequest request) {
        UserInfo user = userService.getThisUser().getUserInfo();

        List<Long> listRequest = request.getFriendsIds();

        listRequest.forEach(user2 ->{
            if (!friendService.isTheyAreFriends(user.getId(), user2)){
                throw new CustomException("Один из пользователей в списке не состоит в друзьях" +
                        " текущего пользователя", HttpStatus.FORBIDDEN);
            }
        });

        listRequest.add(user.getId());

        List<Long> moviesIds = wishRepo.findListSharedMoviesIdsByParam(listRequest, listRequest.size(),
                request.isViewed(), request.getYearFrom(), request.getYearTo(), request.getRatingFrom(), request.getRatingTo());

        Stream<Movie> streamWish = movieService.findSharedMoviesByUsers(moviesIds)
                .stream();

        List<String> genreList = request.getGenreNames();

        if (genreList !=null) {
            streamWish = streamWish.filter(movie -> {
                for (String string: genreList) {
                    if (!movie.getGenresToString().contains(string))
                        return false;
                }
                return true;
            });
        }

        return streamWish.collect(Collectors.toList());
    }
}
