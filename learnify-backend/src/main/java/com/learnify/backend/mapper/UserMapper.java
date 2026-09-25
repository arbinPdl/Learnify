package com.learnify.backend.mapper;

import com.learnify.backend.dto.UserRequestDto;
import com.learnify.backend.dto.UserResponseDto;
import com.learnify.backend.entity.User;
import com.learnify.backend.enums.UserRole;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {
    public User mapRequest(UserRequestDto userRequestDto) {
        String username = userRequestDto.getUsername();
        String password = userRequestDto.getPassword();
        String name = userRequestDto.getName();
        String email = userRequestDto.getEmail();
        UserRole userRole = UserRole.STUDENT;

        return new User(username, password, email, name, userRole);
    }

    public UserResponseDto mapResponse(User user) {
        Long userId = user.getUserId();
        String username = user.getUsername();
        String name = user.getName();
        String email = user.getEmail();
        UserRole userRole = user.getUserRole();
        long reputationPoints = user.getReputationPoints() == null ? 0L : user.getReputationPoints();
        LocalDateTime createdAt = user.getCreatedAt();

        return new UserResponseDto(userId, username, name, email, userRole, reputationPoints, createdAt);
    }


    public User updateUser(User user, UserRequestDto userRequestDto) {
        String username = userRequestDto.getUsername();
        String name = userRequestDto.getName();
        String email = userRequestDto.getEmail();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
