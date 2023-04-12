package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.mapper.EntityMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor
class UserServiceImplTest {

    UserService userService;

    @Autowired
    EntityMapper mapper;

    @MockBean
    UserRepository userRepository;

    User user;
    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository, mapper);
        user = new User(1L, "user", "mail@ya.ru");
        userDto = new UserDto(1L, "user", "mail@ya.ru");
    }

    @Test
    void createUser() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto res = userService.createUser(userDto);

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(userDto.getName(), res.getName());
        assertEquals(userDto.getEmail(), res.getEmail());
    }

    @Test
    void createUser_WithDuplicateEmail() {
        when(userRepository.save(any())).thenThrow(new ConstraintViolationException("", null, ""));

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
    }

    @Test
    void updateUser() {
        UserDto newUserDto = new UserDto(null, "user2", "mail2@ya.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto res = userService.updateUser(newUserDto, userDto.getId());

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(newUserDto.getName(), res.getName());
        assertEquals(newUserDto.getEmail(), res.getEmail());
    }

    @Test
    void updateUser_UpdateName() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(any())).thenReturn(user);
        UserDto newUserDto = new UserDto(null, "user2", null);

        UserDto res = userService.updateUser(newUserDto, userDto.getId());

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(newUserDto.getName(), res.getName());
        assertEquals(userDto.getEmail(), res.getEmail());
    }

    @Test
    void updateUser_UpdateMail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(any())).thenReturn(user);
        UserDto newUserDto = new UserDto(null, null, "mail2@ya.ru");

        UserDto res = userService.updateUser(newUserDto, userDto.getId());

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(userDto.getName(), res.getName());
        assertEquals(newUserDto.getEmail(), res.getEmail());
    }

    @Test
    void updateUser_MailWasUsedByAnotherUser() {
        User user2 = new User (2L, "user2", "mail2@ya.ru");
        UserDto newUserDto = new UserDto(null, null, "mail2@ya.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user2));
        when(userRepository.save(any())).thenReturn(user);

        assertThrows(ConflictException.class, () -> userService.updateUser(newUserDto, userDto.getId()));
    }

    @Test
    void getUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto res = userService.getUser(userDto.getId());

        assertNotNull(res);
        assertEquals(UserDto.class, res.getClass());
        assertEquals(userDto.getId(), res.getId());
        assertEquals(userDto.getName(), res.getName());
        assertEquals(userDto.getEmail(), res.getEmail());
    }

}