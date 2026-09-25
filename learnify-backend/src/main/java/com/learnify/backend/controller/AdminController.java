package com.learnify.backend.controller;

import com.learnify.backend.dto.UserRequestDto;
import com.learnify.backend.dto.UserResponseDto;
import com.learnify.backend.service.AnswerService;
import com.learnify.backend.service.QuestionService;
import com.learnify.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    public AdminController(UserService userService, QuestionService questionService, AnswerService answerService) {
        this.userService = userService;
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @PostMapping("/teacher")
    public ResponseEntity<UserResponseDto> createTeacher(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createTeacher(userRequestDto));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/answer/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }
}
