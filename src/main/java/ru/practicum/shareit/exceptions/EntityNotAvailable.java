package ru.practicum.shareit.exceptions;

public class EntityNotAvailable extends RuntimeException {
    public EntityNotAvailable(final String m) {
        super(m);
    }
}