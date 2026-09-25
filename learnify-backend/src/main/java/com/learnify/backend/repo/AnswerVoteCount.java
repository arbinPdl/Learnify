package com.learnify.backend.repo;

import com.learnify.backend.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public interface AnswerVoteCount {
    Answer getAnswer();
    Long getVoteCount();
}
