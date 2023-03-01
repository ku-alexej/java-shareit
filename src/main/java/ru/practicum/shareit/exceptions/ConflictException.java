package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConflictException extends RuntimeException {
    public ConflictException(final String m) {
        super(m);
    }
}