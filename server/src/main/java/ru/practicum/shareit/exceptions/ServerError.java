package ru.practicum.shareit.exceptions;

public class ServerError extends RuntimeException {
    public ServerError(final String m) {
        super(m);
    }
}
