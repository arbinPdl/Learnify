package com.learnify.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnswerResponseDto {
    private Long answerId;
    QuestionResponseDto questionResponseDto;
    private UserResponseDto userResponseDto;
    private String content;
    private Long voteCount;
}
