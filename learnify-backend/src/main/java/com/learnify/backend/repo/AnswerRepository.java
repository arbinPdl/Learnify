package com.learnify.backend.repo;

import com.learnify.backend.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByAnswerIdAndDeletedFalse(Long answerId);

    Long countByQuestion_QuestionIdAndDeletedFalse(Long questionId);

    List<Answer> findByQuestion_QuestionIdAndDeletedFalse(Long questionId);

    List<Answer> findByUser_UserIdAndDeletedFalse(Long userId);

    @Query("""
        SELECT a AS answer, COUNT(v) AS voteCount
        FROM Answer a
        LEFT JOIN Vote v ON v.answer = a
        WHERE a.question.questionId = :questionId
          AND a.deleted = false
        GROUP BY a
    """)
    List<AnswerVoteCount> findAnswersWithVoteCountByQuestionId(Long questionId);

    @Query("""
        SELECT a AS answer, COUNT(v) AS voteCount
        FROM Answer a
        LEFT JOIN Vote v ON v.answer = a
        WHERE a.user.userId = :userId
          AND a.deleted = false
        GROUP BY a
    """)
    List<AnswerVoteCount> findAnswersWithVoteCountByUserId(Long userId);

}
