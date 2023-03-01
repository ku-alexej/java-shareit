package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private final Long id;
    private final String description;
    private final User requester;
    private final LocalDateTime created;
}
