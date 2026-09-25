package com.learnify.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionDto {
    private LocalDateTime dateTime;
    private Integer statusCode;
    private String message;
    private String path;
}
