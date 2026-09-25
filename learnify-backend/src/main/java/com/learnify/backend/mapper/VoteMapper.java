package com.learnify.backend.mapper;

import com.learnify.backend.dto.VoteResponseDto;
import com.learnify.backend.entity.Answer;
import com.learnify.backend.entity.User;
import com.learnify.backend.entity.Vote;
import org.springframework.stereotype.Component;

@Component
public class VoteMapper {
    UserMapper userMapper;
    VoteMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public VoteResponseDto mapResponse(Vote vote) {
        return new VoteResponseDto(vote.getVoteId(), userMapper.mapResponse(vote.getUser()));
    }
}
