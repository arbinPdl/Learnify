package com.learnify.backend.controller;

import com.learnify.backend.dto.AnswerRequestDto;
import com.learnify.backend.dto.AnswerResponseDto;
import com.learnify.backend.service.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.learnify.backend.security.CustomUserDetails;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerService answerService;
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/question/{questionId}")
    public ResponseEntity<AnswerResponseDto> createAnswer(@Valid @RequestBody AnswerRequestDto answerRequestDto, @PathVariable Long questionId, @AuthenticationPrincipal CustomUserDetails user) {
        AnswerResponseDto answerResponseDto = answerService.createAnswer(answerRequestDto, questionId, user.getUserId(), user.getUserRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(answerResponseDto);
    }

    @PutMapping("/{answerId}")
    public ResponseEntity<AnswerResponseDto> updateAnswer(
            @Valid @RequestBody AnswerRequestDto answerRequestDto,
            @PathVariable Long answerId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        AnswerResponseDto answerResponseDto = answerService.updateAnswer(answerRequestDto, answerId, user.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(answerResponseDto);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<AnswerResponseDto>> getAnswersByQuestionId(@PathVariable Long questionId) {
        List<AnswerResponseDto> answerResponseDto = answerService.getAnswerByQuestionId(questionId);
        return ResponseEntity.ok().body(answerResponseDto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnswerResponseDto>> getAnswersByUserId(@PathVariable Long userId) {
        List<AnswerResponseDto> answerResponseDto = answerService.getAnswerByUserId(userId);
        return ResponseEntity.ok().body(answerResponseDto);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteOwnAnswer(@PathVariable Long answerId, @AuthenticationPrincipal CustomUserDetails user) {
        answerService.deleteOwnAnswer(answerId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

}
