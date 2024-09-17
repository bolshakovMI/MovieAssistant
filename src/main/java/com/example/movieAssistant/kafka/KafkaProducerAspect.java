package com.example.movieAssistant.kafka;

import com.example.movieAssistant.model.dto.event.FriendshipEvent;
import com.example.movieAssistant.model.dto.event.WishCreateEvent;
import com.example.movieAssistant.model.dto.request.WishUpdateRequest;
import com.example.movieAssistant.model.dto.response.FriendshipResponse;
import com.example.movieAssistant.model.dto.response.WishResponse;
import com.example.movieAssistant.model.enums.RequestConfirmationStatus;
import com.example.movieAssistant.services.FriendService;
import com.example.movieAssistant.services.MovieService;
import com.example.movieAssistant.services.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Aspect
@Component
public class KafkaProducerAspect {

    private final KafkaTemplate<String, WishCreateEvent> kafkaWishTemplate;
    private final KafkaTemplate<String, FriendshipEvent> kafkaFriendshipTemplate;
    private final MovieService movieService;
    private final UserInfoService userInfoService;
    private final FriendService friendService;

    @AfterReturning(value = "execution(* com.example.movieAssistant.services.WishParserService.createWishWithParsing(..))",
            returning = "wishResponse")
    public void sendToKafkaIfCreateWish(JoinPoint joinPoint, WishResponse wishResponse) {
        WishCreateEvent wishCreateEvent = eventFromResponse(wishResponse);
        kafkaWishTemplate.send("wish_create", wishCreateEvent);
    }

    @AfterReturning(value = "execution(* com.example.movieAssistant.services.WishService.updateWish" +
            "(com.example.movieAssistant.model.dto.request.WishUpdateRequest, Long))",
            returning = "wishResponse")
    public void sendToKafkaIfMovieViewed(JoinPoint joinPoint, WishResponse wishResponse) {
        Object[] args = joinPoint.getArgs();
        WishUpdateRequest wishUpdateRequest = (WishUpdateRequest) args[0];
        if (wishUpdateRequest.isViewed()){
            WishCreateEvent wishCreateEvent = eventFromResponse(wishResponse);
            kafkaWishTemplate.send("wish_update", wishCreateEvent);
        }
    }

    @AfterReturning(value = "execution(* com.example.movieAssistant.services.FriendService.considerRequest" +
            "(Long, com.example.movieAssistant.model.enums.RequestConfirmationStatus))",
            returning = "friendshipResponse")
    public void sendToKafkaAboutNewFriendship(JoinPoint joinPoint, FriendshipResponse friendshipResponse) {
        Long user1Id = friendshipResponse.getUser1().getId();
        String user1Login = userInfoService.getLoginById(user1Id);
        List<String> user1FriendsLogins = friendService.getListLoginsOfAllUsersFriends(user1Id);

        Long user2Id = friendshipResponse.getUser1().getId();
        String user2Login = userInfoService.getLoginById(user2Id);
        List<String> user2FriendsLogins = friendService.getListLoginsOfAllUsersFriends(user1Id);

        boolean isWithdrawn = friendshipResponse.isWithdrawn();
        RequestConfirmationStatus status = friendshipResponse.getStatus();

        FriendshipEvent friendshipEvent = new FriendshipEvent(user1Id, user1Login, user1FriendsLogins,
                user2Id, user2Login, user2FriendsLogins, isWithdrawn, status);

        kafkaFriendshipTemplate.send("new_friendship", friendshipEvent);
    }

    public WishCreateEvent eventFromResponse(WishResponse wishResponse){
        Long movieId = wishResponse.getMovieId();
        String title = movieService.getMovieById(movieId);

        Long userId = wishResponse.getUserId();
        String login = userInfoService.getLoginById(userId);

        List<String> friendsLogins = friendService.getListLoginsOfAllUsersFriends(userId);

        return new WishCreateEvent(movieId, title, userId, login, friendsLogins);
    }
}

