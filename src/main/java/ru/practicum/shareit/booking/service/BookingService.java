package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.AnswerBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    AnswerBookingDto createBooking(Long userId, BookingDto bookingDto);

    AnswerBookingDto confirmationBooking(Long userId, Long bookingId, Boolean approved);

    AnswerBookingDto getBooking(Long userId, Long bookingId);

    List<AnswerBookingDto> getAllBookingByUser(Long userId, String state, int from, int size);

    List<AnswerBookingDto> getAllBookingByOwner(Long userId, String state, int from, int size);

}