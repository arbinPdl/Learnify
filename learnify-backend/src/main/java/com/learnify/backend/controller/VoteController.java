package com.learnify.backend.controller;

import com.learnify.backend.dto.VoteResponseDto;
import com.learnify.backend.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.learnify.backend.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/vote")
public class VoteController {
    VoteService voteService;
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PutMapping("/answer/{answerId}")
    public ResponseEntity<Void> vote(@PathVariable Long answerId, @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("Controller");
        voteService.vote(answerId, user.getUserId(), user.getUserRole());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<List<VoteResponseDto>> getVotesByAnswerId(@PathVariable Long answerId) {
        List<VoteResponseDto> list = voteService.getVotesByAnswerId(answerId);
        return ResponseEntity.ok().body(list);
    }
}
