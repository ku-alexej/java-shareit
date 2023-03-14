package ru.practicum.shareit.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(final String m) {
        super(m);
    }
}