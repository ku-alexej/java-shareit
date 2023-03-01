package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {

    private Long id;

    private String name;

    @Email(message = "User's email has wrong format")
    @NotBlank(message = "User's email missing")
    private String email;

}