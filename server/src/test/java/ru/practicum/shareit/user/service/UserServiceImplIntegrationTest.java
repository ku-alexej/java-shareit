package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Alex");
        userDto.setEmail("alex@ya.ru");

        UserDto userDtoRespond = userService.createUser(userDto);
        User userRespond = userRepository.findById(userDtoRespond.getId()).orElse(null);

        assertEquals(userDto.getName(), userDtoRespond.getName());
        assertEquals(userDto.getEmail(), userDtoRespond.getEmail());

        assertNotNull(userRespond);
        assertEquals(userDto.getName(), userRespond.getName());
        assertEquals(userDto.getEmail(), userRespond.getEmail());
    }

    @Test
    public void createUser_duplicateEmail() {
        String email = "alex@ya.ru";

        UserDto firstUserDto = new UserDto();
        firstUserDto.setName("Alex");
        firstUserDto.setEmail(email);
        userService.createUser(firstUserDto);

        UserDto secondUserDto = new UserDto();
        secondUserDto.setName("Max");
        secondUserDto.setEmail(email);

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(secondUserDto));
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User oldUser = userRepository.save(user);

        UserDto newUserDto = new UserDto();
        newUserDto.setId(oldUser.getId());
        newUserDto.setName("Max");
        newUserDto.setEmail("max@ya.ru");

        UserDto updatedUser = userService.updateUser(newUserDto, oldUser.getId());

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getId(), newUserDto.getId());
        assertEquals(updatedUser.getName(), newUserDto.getName());
        assertEquals(updatedUser.getEmail(), newUserDto.getEmail());
    }

    @Test
    public void updateUser_userNotInBase() {
        UserDto newUserDto = new UserDto();
        newUserDto.setName("Max");
        newUserDto.setEmail("max@ya.ru");

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(newUserDto, 99L));
    }

    @Test
    public void updateUser_duplicateEmail() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        User user2 = new User();
        user2.setName("Max");
        user2.setEmail("max@ya.ru");
        userRepository.save(user2);

        UserDto userDto = new UserDto();
        userDto.setName("Vlad");
        userDto.setEmail(user2.getEmail());

        assertThrows(ConflictException.class, () -> userService.updateUser(userDto, savedUser.getId()));
    }

    @Test
    void getUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        UserDto userFromDb = userService.getUser(savedUser.getId());
        assertEquals(userFromDb.getEmail(), user.getEmail());
    }

    @Test
    void getUser_beforeSaveUser() {
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(99L));
    }

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setName("Alex");
        user1.setEmail("alex@ya.ru");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Max");
        user2.setEmail("max@ya.ru");
        userRepository.save(user2);

        List<UserDto> usersDtoRespond = userService.getAllUsers();

        assertEquals(usersDtoRespond.size(), 2);
        assertEquals(usersDtoRespond.get(0).getName(), user1.getName());
        assertEquals(usersDtoRespond.get(0).getEmail(), user1.getEmail());
        assertEquals(usersDtoRespond.get(1).getName(), user2.getName());
        assertEquals(usersDtoRespond.get(1).getEmail(), user2.getEmail());
    }

    @Test
    void getAllUsers_beforeAddUsers() {
        List<UserDto> usersDtoRespond = userService.getAllUsers();

        assertEquals(usersDtoRespond.size(), 0);
    }

    @Test
    void deleteUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("max@ya.ru");
        User savedUser = userRepository.save(user);

        userService.deleteUser(savedUser.getId());
        Boolean rez = userRepository.existsById(savedUser.getId());

        assertEquals(rez, false);
    }
}