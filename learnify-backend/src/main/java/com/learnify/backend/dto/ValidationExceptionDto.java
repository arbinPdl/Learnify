package com.learnify.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ValidationExceptionDto {
    private LocalDateTime timestamp;
    private Integer statusCode;
    private String message;
    private String path;
    Map<String, String> fieldErrors;
}
