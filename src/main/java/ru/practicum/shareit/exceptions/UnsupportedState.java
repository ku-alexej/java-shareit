package ru.practicum.shareit.exceptions;

public class UnsupportedState extends RuntimeException {
    public UnsupportedState(final String m) {
        super(m);
    }
}