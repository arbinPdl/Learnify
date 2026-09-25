package com.learnify.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long answerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionId")
    private Question question;

    private String content;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Boolean deleted = false;

    public Answer(User user, Question question, String content) {
        this.user = user;
        this.question = question;
        this.content = content;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
