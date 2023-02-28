package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 * Класс сделан по советам ментора к ТЗ 13
 */
@Data
public class ItemRequest {
    private final Long id;
    private final String description;
    private final User requester;
    private final LocalDateTime created;
}
