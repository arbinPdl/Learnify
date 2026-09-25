package com.learnify.backend.mapper;

import com.learnify.backend.dto.QuestionRequestDto;
import com.learnify.backend.dto.QuestionResponseDto;
import com.learnify.backend.dto.UserResponseDto;
import com.learnify.backend.entity.Question;
import com.learnify.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {
    private UserMapper userMapper;
    public QuestionMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Question mapRequest(QuestionRequestDto questionRequestDto, User user) {
        String content = questionRequestDto.getContent();
        return new Question(user, content);
    }

    public QuestionResponseDto mapResponse(Question question) {
        return mapResponse(question, 0L);
    }

    public QuestionResponseDto mapResponse(Question question, Long answerCount) {
        long questionId = question.getQuestionId();
        UserResponseDto userResponseDto = userMapper.mapResponse(question.getUser());
        String content =  question.getContent();
        return new QuestionResponseDto(questionId, userResponseDto, content, answerCount);
    }

    public Question updateQuestion(QuestionRequestDto questionRequestDto, Question question) {
        String content = questionRequestDto.getContent();
        question.setContent(content);
        return question;
    }
}
