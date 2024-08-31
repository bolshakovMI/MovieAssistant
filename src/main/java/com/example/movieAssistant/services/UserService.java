package com.example.movieAssistant.services;

import com.example.movieAssistant.model.db.entity.User;
import com.example.movieAssistant.model.dto.request.AuthorityRequest;
import com.example.movieAssistant.model.dto.request.PasswordChangeRequest;
import com.example.movieAssistant.model.dto.request.UserRequest;
import com.example.movieAssistant.model.dto.response.AuthorityResponse;
import com.example.movieAssistant.model.dto.response.JwtAuthenticationResponse;
import com.example.movieAssistant.model.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    Page<UserResponse> getAllUsers(Integer page, Integer perPage, String sort, Sort.Direction order);
    User getUser(String user);
    UserResponse getUserResponse(String user);
    User getThisUser();
    JwtAuthenticationResponse createUser(UserRequest request);

    @Transactional
    void createAdmin(String name, String password);

    String updatePassword(PasswordChangeRequest request);
    AuthorityResponse updateAuthority (AuthorityRequest request);
    AuthorityResponse addAuthority (String username, List<String> newAuthority);
    AuthorityResponse  deleteAuthority (String username, List <String> newAuthority);
    String setEnable(String username, boolean isEnable);
}
