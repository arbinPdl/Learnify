package com.learnify.backend.exception;

import com.learnify.backend.dto.ExceptionDto;
import com.learnify.backend.dto.ValidationExceptionDto;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDto> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> fieldError = new HashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldError.put(error.getField(), error.getDefaultMessage())
                );
        ValidationExceptionDto exceptionDto = new ValidationExceptionDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                req.getRequestURL().toString(),
                fieldError
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleException(Exception e, HttpServletRequest request) {
        System.out.println("Dto hit exception");
        ExceptionDto exceptionDto = new ExceptionDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDto);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDto> handleRuntimeException(RuntimeException e, HttpServletRequest req) {
        e.printStackTrace();
        ExceptionDto exceptionDto = new ExceptionDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                req.getRequestURL().toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDto);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ExceptionDto> handleEntityExistsException(Exception e, HttpServletRequest request) {
        ExceptionDto exceptionDto = new ExceptionDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDto);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest req) {
        ExceptionDto exceptionDto = new ExceptionDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                req.getRequestURL().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDto> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        e.printStackTrace();
        ExceptionDto exceptionDto = new ExceptionDto(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDto);
    }
}
