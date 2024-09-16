package com.example.movieAssistant.services;

import com.example.movieAssistant.model.db.entity.UserInfo;
import com.example.movieAssistant.model.dto.request.UserInfoRequest;
import com.example.movieAssistant.model.dto.response.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface UserInfoService {
    Page<UserInfoResponse> getAllUsers(Integer page, Integer perPage, String sort, Sort.Direction order);
    UserInfoResponse getUser(Long id);
    UserInfoResponse updateUser(Long id, UserInfoRequest request);
    UserInfo getUserDb(Long id);
    List<UserInfo> getAllByIds(List<Long> list);

    String getLoginById(Long id);
}
