package com.learnify.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "username needed")
    @Size(min=4, max = 30)
    private String username;

    @NotBlank(message = "password needed")
    @Size(min = 8, max = 16)
    private String password;

    @NotBlank(message = "Name needed")
    @Size(min = 3, max = 30)
    private String name;

    @NotBlank(message = "Email needed")
    @Email(message = "Email needed")
    private String email;

}
