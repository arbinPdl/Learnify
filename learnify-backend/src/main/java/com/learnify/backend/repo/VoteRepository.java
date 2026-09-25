package com.learnify.backend.repo;

import com.learnify.backend.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUser_UserIdAndAnswer_AnswerId(long userId, long answerId);

    List<Vote> findByAnswer_AnswerId(Long answerId);

    Long countByAnswer_AnswerId(Long answerId);

    void deleteByAnswer_AnswerId(Long answerId);

    void deleteByUser_UserId(Long userId);
}
