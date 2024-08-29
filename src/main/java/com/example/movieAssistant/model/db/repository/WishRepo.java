package com.example.movieAssistant.model.db.repository;

import com.example.movieAssistant.model.db.entity.Movie;
import com.example.movieAssistant.model.db.entity.UserInfo;
import com.example.movieAssistant.model.db.entity.Wish;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WishRepo extends JpaRepository<Wish, Long> {
    // Проверяет, есть ли Wish указанного пользователя с указанным фильмом
    boolean existsWishByUsernameAndMovie(UserInfo username, Movie movie);

    @Query("SELECT m.id AS movieId, COUNT(w.id) AS wishCount " +
            "FROM Wish w " +
            "JOIN w.movie m " +
            "WHERE w.viewed = false AND w.deleted = false " +
            "GROUP BY m.id " +
            "ORDER BY wishCount DESC")
    List<Map<String, Object>> findMostPopularMovies(Pageable pageRequest);

    // MY
    // Возвращает один случайный Wish для указанного пользователя
    @Query("SELECT w FROM Wish w " +
            "WHERE w.username = :username AND w.viewed = false AND w.deleted = false " +
            "ORDER BY RANDOM() LIMIT 1")
    Optional<Wish> findRandomWishByUsername(UserInfo username);

    // Возвращает страницу Wish для указанного пользователя
    @Query("SELECT w FROM Wish w " +
            "WHERE w.username = :username AND w.viewed = false AND w.deleted = false " )
    Page<Wish> findPageWishByUsername(UserInfo username, Pageable pageable);

    // Возвращает лист всех фильмов пользователя по 5 параметрам
    @Query("SELECT w FROM Wish w " +
            "WHERE w.username = :username AND w.viewed = :viewed AND w.deleted = false " +
            "AND w.movie.year BETWEEN :yearFrom AND :yearTo " +
            "AND w.movie.rating BETWEEN :ratingFrom AND :ratingTo")
    List<Wish> findListWishByUsernameWithParam(
            @Param("username") UserInfo username,
            @Param("viewed") boolean viewed,
            @Param("yearFrom") short yearFrom,
            @Param("yearTo") short yearTo,
            @Param("ratingFrom") double ratingFrom,
            @Param("ratingTo") double ratingTo
    );

    // OURS
    // Возвращает страницу общих фильмов для пользователей
    @Query(value = "SELECT DISTINCT m.imdb_id FROM wishes w " +
            "JOIN movies m ON w.movie_id = m.imdb_id " +
            "WHERE w.user_id IN :usernames AND w.viewed = false AND w.deleted = false " +
            "GROUP BY m.imdb_id HAVING COUNT(DISTINCT w.user_id) = :usernamesCount",
            nativeQuery = true)
    Page<Long> findPageSharedMoviesIdsByUsers(@Param("usernames") List<Long> usernames,
                                          @Param("usernamesCount") int usernamesCount,
                                          Pageable pageable);

    // Возвращает лист всех общих фильмов для пользователей
    @Query(value = "SELECT DISTINCT m.imdb_id FROM wishes w " +
            "JOIN movies m ON w.movie_id = m.imdb_id " +
            "WHERE w.user_id IN :usernames AND w.viewed = false AND w.deleted = false " +
            "GROUP BY m.imdb_id HAVING COUNT(DISTINCT w.user_id) = :usernamesCount", nativeQuery = true)
    List<Long> findSharedMoviesIdsByUsers(@Param("usernames") List<Long> usernames, @Param("usernamesCount") int usernamesCount);

    // Возвращает лист всех общих фильмов пользователей по 5 параметрам
    @Query(value = "SELECT DISTINCT m.imdb_id FROM wishes w " +
            "JOIN movies m ON w.movie_id = m.imdb_id " +
            "WHERE w.user_id IN :usernames AND w.viewed = :viewed AND w.deleted = false " +
            "AND m.year BETWEEN :yearFrom AND :yearTo " +
            "AND m.rating BETWEEN :ratingFrom AND :ratingTo " +
            "GROUP BY m.imdb_id HAVING COUNT(DISTINCT w.user_id) = :usernamesCount", nativeQuery = true)
    List<Long> findListSharedMoviesIdsByParam(@Param("usernames") List<Long> usernames,
                                              @Param("usernamesCount") int usernamesCount,
                                              @Param("viewed") boolean viewed,
                                              @Param("yearFrom") short yearFrom,
                                              @Param("yearTo") short yearTo,
                                              @Param("ratingFrom") double ratingFrom,
                                              @Param("ratingTo") double ratingTo);
}
