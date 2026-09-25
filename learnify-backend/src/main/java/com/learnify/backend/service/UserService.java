package com.learnify.backend.service;

import com.learnify.backend.dto.UserRequestDto;
import com.learnify.backend.dto.UserResponseDto;
import com.learnify.backend.entity.User;
import com.learnify.backend.enums.UserRole;
import com.learnify.backend.exception.DuplicateResourceException;
import com.learnify.backend.exception.ResourceNotFoundException;
import com.learnify.backend.mapper.UserMapper;
import com.learnify.backend.repo.UserRepository;
import com.learnify.backend.repo.QuestionRepository;
import com.learnify.backend.repo.AnswerRepository;
import com.learnify.backend.repo.VoteRepository;
import com.learnify.backend.entity.Question;
import com.learnify.backend.entity.Answer;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;

    public UserService(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder,
                       QuestionRepository questionRepository, AnswerRepository answerRepository, VoteRepository voteRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
    }

    //Create New User
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        return createUserWithRole(userRequestDto, UserRole.STUDENT);
    }

    @Transactional
    public UserResponseDto createTeacher(UserRequestDto userRequestDto) {
        return createUserWithRole(userRequestDto, UserRole.TEACHER);
    }

    private UserResponseDto createUserWithRole(UserRequestDto userRequestDto, UserRole userRole) {
        if(userRepository.existsByEmailAndDeletedFalse(userRequestDto.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }
        if(userRepository.existsByUsernameAndDeletedFalse(userRequestDto.getUsername())){
            throw new DuplicateResourceException("Username already exists");
        }

        User user = userMapper.mapRequest(userRequestDto);
        user.setUserRole(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.mapResponse(userRepository.save(user));
    }

    //Update User
    @Transactional
    public UserResponseDto updateUser(UserRequestDto userRequestDto, Long userId) {
       User user = userRepository
               .findByUserIdAndDeletedFalse(userId)
               .orElseThrow(()-> new ResourceNotFoundException("User not found"));
       if (userRepository.existsByEmailAndDeletedFalse(userRequestDto.getEmail()) && !user.getEmail().equals(userRequestDto.getEmail())) {
           throw new DuplicateResourceException("Email already exists");
       }
       if (userRepository.existsByUsernameAndDeletedFalse(userRequestDto.getUsername()) && !user.getUsername().equals(userRequestDto.getUsername())) {
           throw new DuplicateResourceException("Username already exists");
       }
       User updatedUser = userMapper.updateUser(user, userRequestDto);
       if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isBlank()) {
           updatedUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
       }
       return userMapper.mapResponse(userRepository.save(updatedUser));
    }

    @Transactional
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository
                .findByUserIdAndDeletedFalse(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return userMapper.mapResponse(user);
    }

    @Transactional
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository
                .findByUsernameAndDeletedFalse(username)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return userMapper.mapResponse(user);
    }
    @Transactional
    public List<UserResponseDto> getTopUsers() {
        List<User> users = userRepository.findTopStudentsByReputation().stream().limit(10).toList();
        return users
                .stream()
                .map(userMapper::mapResponse).
                toList();
    }
    @Transactional
    public void softDeleteUser(Long userId) {
        User user = userRepository
                .findByUserIdAndDeletedFalse(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        voteRepository.deleteByUser_UserId(userId);
        for (Question question : questionRepository.findByUser_UserIdAndDeletedFalseOrderByCreatedDateDesc(userId)) {
            for (Answer answer : answerRepository.findByQuestion_QuestionIdAndDeletedFalse(question.getQuestionId())) {
                voteRepository.deleteByAnswer_AnswerId(answer.getAnswerId());
                answer.setDeleted(true);
            }
            question.setDeleted(true);
        }
        for (Answer answer : answerRepository.findByUser_UserIdAndDeletedFalse(userId)) {
            voteRepository.deleteByAnswer_AnswerId(answer.getAnswerId());
            answer.setDeleted(true);
        }
        user.setDeleted(true);
    }

    @Transactional
    public void deleteUser(Long userId) {
        softDeleteUser(userId);
    }

}
