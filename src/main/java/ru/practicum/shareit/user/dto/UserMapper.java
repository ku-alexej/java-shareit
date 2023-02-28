package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

@Data
@Slf4j
public class UserMapper {

    public static User toUser(UserDto userDto) {
        log.info("DTO to user");
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        log.info("User to DTO");
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

}