package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {

    private Long id;

    private String name;

    @Email(message = "User's email has wrong format")
    @NotBlank(message = "User's email missing")
    private String email;

}