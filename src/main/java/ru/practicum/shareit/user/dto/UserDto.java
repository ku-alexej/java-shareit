package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    @Email(message = "User's email has wrong format")
    @NotBlank(message = "User's email missing")
    private String email;

}