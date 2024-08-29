package com.example.movieAssistant.services;

import com.example.movieAssistant.model.dto.request.WishParseRequest;
import com.example.movieAssistant.model.dto.response.WishResponse;

public interface WishParserService extends WishService {
    WishResponse createWishWithParsing(WishParseRequest request);
}
