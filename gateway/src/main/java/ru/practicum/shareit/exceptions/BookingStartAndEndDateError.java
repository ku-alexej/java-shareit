package ru.practicum.shareit.exceptions;

public class BookingStartAndEndDateError extends RuntimeException {
    public BookingStartAndEndDateError(final String m) {
        super(m);
    }
}