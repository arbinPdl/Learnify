package com.learnify.backend.mapper;

import com.learnify.backend.dto.AnswerRequestDto;
import com.learnify.backend.dto.AnswerResponseDto;
import com.learnify.backend.dto.QuestionResponseDto;
import com.learnify.backend.dto.UserResponseDto;
import com.learnify.backend.entity.Answer;
import com.learnify.backend.entity.Question;
import com.learnify.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {
    UserMapper userMapper;
    QuestionMapper questionMapper;
    public AnswerMapper(UserMapper userMapper, QuestionMapper questionMapper) {
        this.userMapper = userMapper;
        this.questionMapper = questionMapper;
    }

    public Answer mapRequest(AnswerRequestDto answerRequestDto, User user, Question question) {
        return new Answer(user, question, answerRequestDto.getContent());
    }

    public AnswerResponseDto mapResponse(Answer answer,  long voteCount) {
        UserResponseDto userResponseDto = userMapper.mapResponse(answer.getUser());
        QuestionResponseDto questionResponseDto = questionMapper.mapResponse(answer.getQuestion());
        String content = answer.getContent();
        return new AnswerResponseDto(
                answer.getAnswerId(),
                questionResponseDto,
                userResponseDto,
                content,
                voteCount
        );
    }

    public Answer updateAnswer(AnswerRequestDto answerRequestDto, Answer answer) {
        answer.setContent(answerRequestDto.getContent());
        return answer;
    }
}
