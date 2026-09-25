package com.learnify.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionResponseDto {
    private Long questionId;
    private UserResponseDto user;
    private String content;
    private Long answerCount;
}
