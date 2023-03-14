package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.mapper.EntityMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.toUser(userDto);
        try {
            return mapper.toUserDto(userRepository.save(user));
        } catch (ConstraintViolationException e) {
            throw new ConflictException("Mail " + userDto.getEmail() + " already used by another user");
        }
    }

    @Override
    public UserDto updateUser(UserDto newUserDto, Long userId) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " does not exist"));
        if (!isUsersMailInBase(newUserDto) || newUserDto.getEmail().equals(oldUser.getEmail())) {
            newUserDto.setId(userId);
            User user = mapper.updatedUser(newUserDto, oldUser);
            userRepository.save(user);
            log.info("User ID {} was updated", newUserDto.getId());
            return mapper.toUserDto(user);
        } else {
            throw new ConflictException("Mail " + newUserDto.getEmail() + " already used by another user");
        }
    }

    @Override
    public UserDto getUser(Long id) throws EntityNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " does not exist"));
        log.info("User with ID {} was founded", id);
        return mapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User with ID {} was deleted", id);

    }

    private boolean isUsersMailInBase(UserDto userDto) {
        List<UserDto> repeats = getAllUsers().stream()
                .filter(u -> u.getEmail().equals(userDto.getEmail()))
                .collect(Collectors.toList());
        return repeats.size() != 0;
    }

}