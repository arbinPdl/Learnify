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
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Boolean deleted =  false;

    public Question(User user, String content) {
        this.user = user;
        this.content = content;
    }

    @PrePersist
    public void prePersist(){
        this.createdDate = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate(){
        this.updatedDate = LocalDateTime.now();
    }
}
