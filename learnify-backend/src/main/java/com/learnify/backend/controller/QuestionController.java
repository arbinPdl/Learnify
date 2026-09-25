package com.learnify.backend.controller;

import com.learnify.backend.dto.QuestionRequestDto;
import com.learnify.backend.dto.QuestionResponseDto;
import com.learnify.backend.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.learnify.backend.security.CustomUserDetails;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    private final QuestionService questionService;
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestion(@Valid @RequestBody QuestionRequestDto questionRequestDto, @AuthenticationPrincipal CustomUserDetails user) {
        QuestionResponseDto questionResponseDto = questionService.createQuestion(questionRequestDto, user.getUserId(), user.getUserRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(questionResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @Valid @RequestBody QuestionRequestDto questionRequestDto,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        QuestionResponseDto questionResponseDto = questionService.updateQuestion(questionRequestDto, id, user.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(questionResponseDto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionByUserId(@PathVariable Long userId) {
        List<QuestionResponseDto> list = questionService.getQuestionByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestions() {
        List<QuestionResponseDto> list = questionService.getAllQuestions();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwnQuestion(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        questionService.deleteOwnQuestion(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }

}
