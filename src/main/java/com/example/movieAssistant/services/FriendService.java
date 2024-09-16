package com.example.movieAssistant.services;

import com.example.movieAssistant.model.db.entity.Friendship;
import com.example.movieAssistant.model.db.entity.UserInfo;
import com.example.movieAssistant.model.dto.response.FriendshipResponse;
import com.example.movieAssistant.model.enums.RequestConfirmationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface FriendService {
    FriendshipResponse sendRequest(Long id);
    FriendshipResponse considerRequest(Long id, RequestConfirmationStatus status);
    FriendshipResponse alterStatus(Long id, boolean status);
    Page<FriendshipResponse> getAllUsersFriends(Integer page, Integer perPage, String sort, Sort.Direction order, Long userId);

    List<Friendship> getListAllUsersFriends(Long userId);

    List<String> getListLoginsOfAllUsersFriends(Long userId);

    Page<FriendshipResponse> getMyAllFriends(Integer page, Integer perPage, String sort, Sort.Direction order);
    Page<FriendshipResponse> getUsersOutgoingRequests(Integer page, Integer perPage, String sort, Sort.Direction order, Long userId, boolean withdrawn, RequestConfirmationStatus status);
    Page<FriendshipResponse> getMyOutgoingRequests(Integer page, Integer perPage, String sort, Sort.Direction order, boolean withdrawn, RequestConfirmationStatus status);
    Page<FriendshipResponse> getMyIncomingRequests(Integer page, Integer perPage, String sort, Sort.Direction order, boolean withdrawn, RequestConfirmationStatus status);
    Page<FriendshipResponse> getUsersIncomingRequests(Integer page, Integer perPage, String sort, Sort.Direction order, Long userId, boolean withdrawn, RequestConfirmationStatus status);
    boolean isTheyAreFriends(Long user1Id, Long user2Id);
    boolean isTheyAreFriends(UserInfo user1Id, UserInfo user2Id);
    Page<FriendshipResponse> getAllFriendships(Integer page, Integer perPage, String sort, Sort.Direction order, boolean withdrawn, RequestConfirmationStatus status);
    FriendshipResponse getStatusBetweenUsers(Long user1Id, Long user2Id);
    FriendshipResponse getStatusWithUser(Long userId);
}
