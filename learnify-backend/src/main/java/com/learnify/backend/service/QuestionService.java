package com.learnify.backend.service;

import com.learnify.backend.dto.QuestionRequestDto;
import com.learnify.backend.dto.QuestionResponseDto;
import com.learnify.backend.entity.Question;
import com.learnify.backend.entity.User;
import com.learnify.backend.exception.ResourceNotFoundException;
import com.learnify.backend.mapper.QuestionMapper;
import com.learnify.backend.repo.QuestionRepository;
import com.learnify.backend.repo.UserRepository;
import com.learnify.backend.repo.AnswerRepository;
import com.learnify.backend.repo.VoteRepository;
import com.learnify.backend.entity.Answer;
import com.learnify.backend.enums.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;
    public QuestionService(
            QuestionRepository questionRepository,
            UserRepository userRepository,
            QuestionMapper questionMapper,
            AnswerRepository answerRepository,
            VoteRepository voteRepository
    ) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.questionMapper = questionMapper;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public QuestionResponseDto createQuestion(QuestionRequestDto questionRequestDto, Long authenticatedUserId, UserRole userRole) {
        if (userRole != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can post questions");
        }
        User user = userRepository
                .findByUserIdAndDeletedFalse(authenticatedUserId)
                .orElseThrow(()-> new ResourceNotFoundException("No user Exists"));
        Question question = questionRepository.save(questionMapper.mapRequest(questionRequestDto, user));
        return questionMapper.mapResponse(question, 0L);
    }

    @Transactional
    public List<QuestionResponseDto> getQuestionByUserId(Long userId) {
        List<Question> list = questionRepository.findByUser_UserIdAndDeletedFalseOrderByCreatedDateDesc(userId);
        return list.stream()
                .map(question -> questionMapper.mapResponse(question, answerRepository.countByQuestion_QuestionIdAndDeletedFalse(question.getQuestionId())))
                .toList();
    }

    @Transactional
    public List<QuestionResponseDto> getAllQuestions() {
        List<Question> list = questionRepository.findByDeletedFalseOrderByCreatedDateDesc();
        return list.stream()
                .map(question -> questionMapper.mapResponse(question, answerRepository.countByQuestion_QuestionIdAndDeletedFalse(question.getQuestionId())))
                .toList();
    }

    @Transactional
    public QuestionResponseDto updateQuestion(QuestionRequestDto questionRequestDto, Long questionId, Long authenticatedUserId) {
        Question question= questionRepository
                .findByQuestionIdAndDeletedFalse(questionId)
                .orElseThrow(()-> new ResourceNotFoundException("Question not found"));
        if (!question.getUser().getUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You can only modify your own questions");
        }
        return questionMapper.mapResponse(questionMapper.updateQuestion(questionRequestDto, question));
    }

    @Transactional
    public void softDeleteQuestion(Long questionId) {
        Question question = questionRepository
                .findByQuestionIdAndDeletedFalse(questionId)
                .orElseThrow(()-> new ResourceNotFoundException("Question not found"));
        deleteQuestionAndAnswers(question);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findByQuestionIdAndDeletedFalse(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        deleteQuestionAndAnswers(question);
    }

    @Transactional
    public void deleteOwnQuestion(Long questionId, Long authenticatedUserId) {
        Question question = questionRepository.findByQuestionIdAndDeletedFalse(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (!question.getUser().getUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You can only delete your own questions");
        }
        deleteQuestionAndAnswers(question);
    }

    private void deleteQuestionAndAnswers(Question question) {
        List<Answer> answers = answerRepository.findByQuestion_QuestionIdAndDeletedFalse(question.getQuestionId());
        for (Answer answer : answers) {
            voteRepository.deleteByAnswer_AnswerId(answer.getAnswerId());
            answer.setDeleted(true);
        }
        question.setDeleted(true);
    }
}
