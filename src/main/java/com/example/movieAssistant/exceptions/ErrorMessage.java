package com.example.movieAssistant.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    private String message;
    public ErrorMessage(String message) {
        this.message = message;
    }
}
