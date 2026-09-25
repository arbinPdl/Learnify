package com.learnify.backend.service;

import com.learnify.backend.dto.AnswerRequestDto;
import com.learnify.backend.dto.AnswerResponseDto;
import com.learnify.backend.entity.Answer;
import com.learnify.backend.entity.Question;
import com.learnify.backend.entity.User;
import com.learnify.backend.exception.ResourceNotFoundException;
import com.learnify.backend.mapper.AnswerMapper;
import com.learnify.backend.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import com.learnify.backend.enums.UserRole;

import java.util.List;

@Service
public class AnswerService {
    AnswerRepository answerRepository;
    UserRepository userRepository;
    QuestionRepository questionRepository;
    VoteRepository voteRepository;
    AnswerMapper answerMapper;
    public AnswerService(
            AnswerRepository answerRepository,
            UserRepository userRepository,
            QuestionRepository questionRepository,
            VoteRepository voteRepository,
            AnswerMapper answerMapper) {
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.voteRepository = voteRepository;
        this.answerMapper = answerMapper;
    }

    @Transactional
    public AnswerResponseDto createAnswer(AnswerRequestDto answerRequestDto, Long questionId, Long authenticatedUserId, UserRole userRole) {
        System.out.println("Answer Create methos hit");
        if (userRole == UserRole.ADMIN) {
            throw new AccessDeniedException("Admins cannot post answers");
        }
        User user = userRepository
                .findByUserIdAndDeletedFalse(authenticatedUserId)
                .orElseThrow(()-> new ResourceNotFoundException("User not Found"));
        Question question = questionRepository
                .findByQuestionIdAndDeletedFalse(questionId)
                .orElseThrow(()-> new ResourceNotFoundException("Question not Found"));

        Answer answer = answerRepository.save(answerMapper.mapRequest(answerRequestDto, user, question));
        long voteCount = 0;
        return answerMapper.mapResponse(answer, voteCount);
    }

    @Transactional
    public AnswerResponseDto updateAnswer(AnswerRequestDto answerRequestDto, Long answerId, Long authenticatedUserId) {
        Answer answer = answerRepository
                .findByAnswerIdAndDeletedFalse(answerId)
                .orElseThrow(()-> new ResourceNotFoundException("Answer not Found"));
        if (!answer.getUser().getUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You can only modify your own answers");
        }
        answerRepository.save(answerMapper.updateAnswer(answerRequestDto, answer));
        long voteCount = voteRepository.countByAnswer_AnswerId(answerId);
        return answerMapper.mapResponse(answer, voteCount);
    }

    @Transactional
    public List<AnswerResponseDto> getAnswerByQuestionId(Long questionId) {
        List<AnswerVoteCount> answerVoteCounts = answerRepository.findAnswersWithVoteCountByQuestionId(questionId);
        return answerVoteCounts
                .stream()
                .map(result ->
                        answerMapper.mapResponse(
                                result.getAnswer(),
                                result.getVoteCount()
                        )
                )
                .toList();
    }

    @Transactional
    public List<AnswerResponseDto> getAnswerByUserId(Long userId) {
        List<AnswerVoteCount> answerVoteCounts = answerRepository.findAnswersWithVoteCountByUserId(userId);
        return answerVoteCounts
                .stream()
                .map(result ->
                        answerMapper.mapResponse(
                                result.getAnswer(),
                                result.getVoteCount()
                        )
                )
                .toList();
    }

    @Transactional
    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findByAnswerIdAndDeletedFalse(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        deleteAnswerAndVotes(answer);
    }

    @Transactional
    public void deleteOwnAnswer(Long answerId, Long authenticatedUserId) {
        Answer answer = answerRepository.findByAnswerIdAndDeletedFalse(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        if (!answer.getUser().getUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You can only delete your own answers");
        }
        deleteAnswerAndVotes(answer);
    }

    private void deleteAnswerAndVotes(Answer answer) {
        voteRepository.deleteByAnswer_AnswerId(answer.getAnswerId());
        answer.setDeleted(true);
    }

}
