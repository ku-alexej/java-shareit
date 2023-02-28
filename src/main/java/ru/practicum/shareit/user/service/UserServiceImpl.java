package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserStorage userStorage;

    @Override
    public UserDto createUser(User u) {
        return UserMapper.toUserDto(userStorage.createUser(u));
    }

    @Override
    public UserDto updateUser(User u, Long userId) {
        return UserMapper.toUserDto(userStorage.updateUser(u, userId));
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(userStorage.getUser(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

}