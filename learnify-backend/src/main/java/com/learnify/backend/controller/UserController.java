package com.learnify.backend.controller;

import com.learnify.backend.dto.UserRequestDto;
import com.learnify.backend.dto.UserResponseDto;
import com.learnify.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.learnify.backend.security.CustomUserDetails;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserRequestDto userRequestDto, @AuthenticationPrincipal CustomUserDetails user) {
        UserResponseDto userResponseDto = userService.updateUser(userRequestDto, user.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        UserResponseDto userResponseDto = userService.getUserByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @GetMapping("/top10")
    public ResponseEntity<List<UserResponseDto>> getTop10Users() {
        List<UserResponseDto> list = userService.getTopUsers();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal CustomUserDetails user) {
        userService.deleteUser(user.getUserId());
        return ResponseEntity.noContent().build();
    }

}
