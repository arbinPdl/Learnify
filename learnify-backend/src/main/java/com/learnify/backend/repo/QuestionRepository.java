package com.learnify.backend.repo;

import com.learnify.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    public List<Question> findByUser_UserIdAndDeletedFalseOrderByCreatedDateDesc(Long userId);

    public Optional<Question> findByQuestionIdAndDeletedFalse(Long questionId);

    public List<Question> findByDeletedFalseOrderByCreatedDateDesc();
}
