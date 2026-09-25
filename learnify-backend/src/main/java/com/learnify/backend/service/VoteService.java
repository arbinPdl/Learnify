package com.learnify.backend.service;

import com.learnify.backend.dto.VoteResponseDto;
import com.learnify.backend.entity.Answer;
import com.learnify.backend.entity.User;
import com.learnify.backend.entity.Vote;
import com.learnify.backend.enums.UserRole;
import com.learnify.backend.mapper.VoteMapper;
import com.learnify.backend.repo.AnswerRepository;
import com.learnify.backend.repo.UserRepository;
import com.learnify.backend.repo.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final VoteMapper voteMapper;

    public VoteService(VoteRepository voteRepository, UserRepository userRepository, AnswerRepository answerRepository, VoteMapper voteMapper) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.voteMapper = voteMapper;
    }

    @Transactional
    public void vote(Long answerId, Long authenticatedUserId, UserRole userRole) {
        System.out.println("VoteService");
        if (userRole == UserRole.ADMIN) {
            throw new AccessDeniedException("Admins cannot vote");
        }
        long weight = userRole == UserRole.TEACHER ? 4 : 2;
        long userId = authenticatedUserId;
        Vote vote = voteRepository.findByUser_UserIdAndAnswer_AnswerId(userId, answerId).orElse(null);
        if(vote != null){
            // A vote changes the reputation of the answer author, never the voter.
            User answerUser = vote.getAnswer().getUser();
            answerUser.setReputationPoints(answerUser.getReputationPoints() - weight);
            voteRepository.delete(vote);
        } else {
            User user =  userRepository.findByUserIdAndDeletedFalse(userId)
                    .orElseThrow(() -> new com.learnify.backend.exception.ResourceNotFoundException("User not found"));
            Answer answer = answerRepository.findByAnswerIdAndDeletedFalse(answerId)
                    .orElseThrow(() -> new com.learnify.backend.exception.ResourceNotFoundException("Answer not found"));
            User answerUser = answer.getUser();
            answerUser.setReputationPoints(answerUser.getReputationPoints() + weight);
            voteRepository.save(new Vote(answer, user));
        }
    }

    @Transactional
    public List<VoteResponseDto> getVotesByAnswerId(Long answerId) {
        List<Vote> list = voteRepository.findByAnswer_AnswerId(answerId);
        return list.stream()
                .map(voteMapper::mapResponse)
                .toList();
    }
}
