package com.example.movieAssistant.services.impl;

import com.example.movieAssistant.exceptions.CustomException;
import com.example.movieAssistant.model.db.entity.Friendship;
import com.example.movieAssistant.model.db.entity.UserInfo;
import com.example.movieAssistant.model.db.repository.FriendRepo;
import com.example.movieAssistant.model.dto.response.FriendshipResponse;
import com.example.movieAssistant.model.enums.RequestConfirmationStatus;
import com.example.movieAssistant.services.FriendService;
import com.example.movieAssistant.services.UserInfoService;
import com.example.movieAssistant.services.UserService;
import com.example.movieAssistant.utils.PaginationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class FriendServiceImpl implements FriendService {

    private final UserInfoService userInfoService;
    private final UserService userService;
    private final FriendRepo friendRepo;
    private final ObjectMapper mapper;

    @Override
    public FriendshipResponse sendRequest(Long id) {
        UserInfo friend = userInfoService.getUserDb(id);

        UserInfo user = userService.getThisUser().getUserInfo();

        if (user.getId().equals(friend.getId())) {
            throw new CustomException("Нельзя направить заявку на добавление в друзья себе", HttpStatus.BAD_REQUEST);
        }

        if (friendRepo.existsFriendshipBetween(friend.getId(), user.getId())) {
            throw new CustomException("Заявка пользователю уже направлена/вы уже в друзьях", HttpStatus.BAD_REQUEST);
        }

        Friendship friendship = new Friendship(user, friend, false, RequestConfirmationStatus.UNCONSIDERED, LocalDateTime.now());
        friendship = friendRepo.save(friendship);

        FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
        response.getUser1().setBirthDay(
                friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        response.getUser2().setBirthDay(
                friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return response;
    }

    @Override
    public FriendshipResponse considerRequest(Long id, RequestConfirmationStatus status) {
        Friendship friendship = friendRepo.findById(id).orElseThrow(() -> new CustomException("Заявка не найдена", HttpStatus.NOT_FOUND));

        UserInfo userInfo = userService.getThisUser().getUserInfo();

        if (!friendship.getUser2().getId().equals(userInfo.getId())) {
            throw new CustomException("Данная заявка адресована другому пользователю", HttpStatus.FORBIDDEN);
        }

        if (friendship.isWithdrawn()) {
            throw new CustomException("Пользователь уже отозвал свою заявку в друзья", HttpStatus.CONFLICT);
        }

        friendship.setStatus(status);
        friendship.setUpdatedAt(LocalDateTime.now());
        friendship = friendRepo.save(friendship);


        FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
        response.getUser1().setBirthDay(
                friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        response.getUser2().setBirthDay(
                friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return response;
    }

    @Override
    public FriendshipResponse alterStatus(Long id, boolean status){
        UserInfo friend = userInfoService.getUserDb(id);

        UserInfo userInfo = userService.getThisUser().getUserInfo();
        Friendship friendship = friendRepo.findFriendshipBetween(userInfo.getId(), friend.getId())
                .orElseThrow(() -> new CustomException("Отсутствует запись об отношениях между данными пользователями", HttpStatus.NOT_FOUND));

        if (friendship.getUser1().getId().equals(userInfo.getId())) {
            friendship.setWithdrawn(!status);
        } else {
            if (status) {
                friendship.setStatus(RequestConfirmationStatus.ACCEPTED);
            } else {
                friendship.setStatus(RequestConfirmationStatus.REJECTED);
            }
        }
        friendship.setUpdatedAt(LocalDateTime.now());
        friendship = friendRepo.save(friendship);


        FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
        response.getUser1().setBirthDay(
                friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        response.getUser2().setBirthDay(
                friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return response;
    }

    @Override
    public Page<FriendshipResponse> getAllUsersFriends(Integer page, Integer perPage, String sort, Sort.Direction order, Long userId) {
        Pageable request = PaginationUtil.getPageRequest(page, perPage, sort, order);

        userInfoService.getUserDb(userId);

        List <FriendshipResponse> all = friendRepo.findByUserIdAndWithdrawnAndStatus(userId, false, RequestConfirmationStatus.ACCEPTED, request)
                .getContent()
                .stream()
                .map(friendship -> {
                    FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
                    response.getUser1().setBirthDay(
                            friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    response.getUser2().setBirthDay(
                            friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }

    @Override
    public List<Friendship> getListAllUsersFriends(Long userId){
        userInfoService.getUserDb(userId);
        return friendRepo.findByUserIdAndWithdrawnAndStatus(userId, false, RequestConfirmationStatus.ACCEPTED);
    }

    @Override
    public List<String> getListLoginsOfAllUsersFriends(Long userId){
        List<Friendship> friendsList = getListAllUsersFriends(userId);

        List<String> loginsList;
        return friendsList
                .stream()
                .map(friendship -> {
                    UserInfo user1 = friendship.getUser1();

                    if (Objects.equals(user1.getId(), userId)){
                        return friendship.getUser2();
                    } else return user1;
                })
                .map(userInfo ->{
                    return userInfo.getLogin().getUsername();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<FriendshipResponse> getMyAllFriends(Integer page, Integer perPage, String sort, Sort.Direction order) {
        UserInfo userInfo = userService.getThisUser().getUserInfo();

        return getAllUsersFriends(page, perPage, sort, order, userInfo.getId());
    }

    @Override
    public Page<FriendshipResponse> getUsersOutgoingRequests(Integer page, Integer perPage, String sort, Sort.Direction order, Long userId, boolean withdrawn, RequestConfirmationStatus status) {
        Pageable request = PaginationUtil.getPageRequest(page, perPage, sort, order);

        UserInfo userInfo = userInfoService.getUserDb(userId);

        List <FriendshipResponse> all = friendRepo.findByUser1AndStatusAndWithdrawn(userInfo, status, withdrawn, request)
                .getContent()
                .stream()
                .map(friendship -> {
                    FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
                    response.getUser1().setBirthDay(
                            friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    response.getUser2().setBirthDay(
                            friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }

    @Override
    public Page<FriendshipResponse> getMyOutgoingRequests(Integer page, Integer perPage, String sort, Sort.Direction order, boolean withdrawn, RequestConfirmationStatus status) {
        Pageable request = PaginationUtil.getPageRequest(page, perPage, sort, order);

        UserInfo userInfo = userService.getThisUser().getUserInfo();

        List <FriendshipResponse> all = friendRepo.findByUser1AndStatusAndWithdrawn(userInfo, status, withdrawn, request)
                .getContent()
                .stream()
                .map(friendship -> {
                    FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
                    response.getUser1().setBirthDay(
                            friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    response.getUser2().setBirthDay(
                            friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }

    @Override
    public Page<FriendshipResponse> getMyIncomingRequests(Integer page, Integer perPage, String sort, Sort.Direction order, boolean withdrawn, RequestConfirmationStatus status) {
        Pageable request = PaginationUtil.getPageRequest(page, perPage, sort, order);

        UserInfo userInfo = userService.getThisUser().getUserInfo();

        List <FriendshipResponse> all = friendRepo.findByUser2AndStatusAndWithdrawn(userInfo, status, withdrawn, request)
                .getContent()
                .stream()
                .map(friendship -> {
                    FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
                    response.getUser1().setBirthDay(
                            friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    response.getUser2().setBirthDay(
                            friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }

    @Override
    public Page<FriendshipResponse> getUsersIncomingRequests(Integer page, Integer perPage, String sort, Sort.Direction order, Long userId, boolean withdrawn, RequestConfirmationStatus status) {
        Pageable request = PaginationUtil.getPageRequest(page, perPage, sort, order);

        UserInfo userInfo = userInfoService.getUserDb(userId);

        List <FriendshipResponse> all = friendRepo.findByUser2AndStatusAndWithdrawn(userInfo, status, withdrawn, request)
                .getContent()
                .stream()
                .map(friendship -> {
                    FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
                    response.getUser1().setBirthDay(
                            friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    response.getUser2().setBirthDay(
                            friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }

    @Override
    public FriendshipResponse getStatusBetweenUsers(Long user1Id, Long user2Id) {
        UserInfo userInfo1 = userInfoService.getUserDb(user1Id);
        UserInfo userInfo2 = userInfoService.getUserDb(user2Id);

        Friendship friendship = friendRepo.findFriendshipBetween(userInfo1.getId(), userInfo2.getId())
                .orElseThrow(() -> new CustomException("Отсутствует запись об отношениях между данными пользователями", HttpStatus.NOT_FOUND));

        FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
        response.getUser1().setBirthDay(
                friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        response.getUser2().setBirthDay(
                friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return response;
    }

    @Override
    public FriendshipResponse getStatusWithUser(Long userId) {
        UserInfo userInfo1 = userService.getThisUser().getUserInfo();

        UserInfo userInfo2 = userInfoService.getUserDb(userId);

        Friendship friendship = friendRepo.findFriendshipBetween(userInfo1.getId(), userInfo2.getId())
                .orElseThrow(() -> new CustomException("Отсутствует запись об отношениях между данными пользователями", HttpStatus.NOT_FOUND));

        FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
        response.getUser1().setBirthDay(
                friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        response.getUser2().setBirthDay(
                friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return response;
    }

    @Override
    public boolean isTheyAreFriends(Long user1Id, Long user2Id) {
        UserInfo userInfo1 = userInfoService.getUserDb(user1Id);
        UserInfo userInfo2 = userInfoService.getUserDb(user2Id);

        Optional<Friendship> optionalFriendship = friendRepo.findFriendshipBetween(userInfo1.getId(), userInfo2.getId());
        if(optionalFriendship.isEmpty()){
            return false;
        }

        Friendship friendship = optionalFriendship.get();

        return !friendship.isWithdrawn() && friendship.getStatus() == RequestConfirmationStatus.ACCEPTED;
    }

    @Override
    public boolean isTheyAreFriends(UserInfo user1Id, UserInfo user2Id) {
        Optional<Friendship> optionalFriendship = friendRepo.findFriendshipBetween(user1Id.getId(), user2Id.getId());
        if(optionalFriendship.isEmpty()){
            return false;
        }

        Friendship friendship = optionalFriendship.get();

        return !friendship.isWithdrawn() && friendship.getStatus() == RequestConfirmationStatus.ACCEPTED;
    }

    @Override
    public Page<FriendshipResponse> getAllFriendships(Integer page, Integer perPage, String sort, Sort.Direction order, boolean withdrawn, RequestConfirmationStatus status) {
        Pageable request = PaginationUtil.getPageRequest(page, perPage, sort, order);

        List <FriendshipResponse> all = friendRepo.findAllByStatusAndWithdrawn(status, withdrawn, request)
                .getContent()
                .stream()
                .map(friendship -> {
                    FriendshipResponse response = mapper.convertValue(friendship, FriendshipResponse.class);
                    response.getUser1().setBirthDay(
                            friendship.getUser1().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    response.getUser2().setBirthDay(
                            friendship.getUser2().getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(all);
    }
}
