package ru.practicum.shareit.user.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User createUser(User user) {
        if (isUsersMailInBase(user)) {
            throw new ConflictException("Mail \"" + user.getEmail() + "\" already used by another user");
        }
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("New user got ID: {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user, Long id) {
        if (users.containsKey(id)) {
            if (!isUsersMailInBase(user) || user.getEmail().equals(users.get(id).getEmail())) {
                user.setId(id);
                if (user.getName() == null) {
                    user.setName(users.get(id).getName());
                }
                if (user.getEmail() == null) {
                    user.setEmail(users.get(id).getEmail());
                }
                users.put(user.getId(), user);
                log.info("User ID {} was updated", user.getId());
                return user;
            } else {
                throw new ConflictException("Mail \"" + user.getEmail() + "\" already used by another user");
            }
        } else {
            throw new EntityNotFoundException("User with ID " + id + " does not exist");
        }
    }

    @Override
    public User getUser(Long id) {
        if (users.containsKey(id)) {
            log.info("User with ID {} was founded", id);
            return users.get(id);
        } else {
            throw new EntityNotFoundException("User with ID " + id + " does not exist");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("User with ID {} was deleted", id);
        } else {
            throw new EntityNotFoundException("User with ID " + id + " does not exist");
        }
    }

    private boolean isUsersMailInBase(User user) {
        List<User> repeats = users.values()
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
        return repeats.size() != 0;
    }

}