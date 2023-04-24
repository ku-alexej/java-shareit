package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.AnswerBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    AnswerBookingDto createBooking(Long userId, BookingDto bookingDto);

    AnswerBookingDto confirmationBooking(Long userId, Long bookingId, Boolean approved);

    AnswerBookingDto getBooking(Long userId, Long bookingId);

    List<AnswerBookingDto> getAllBookingByUser(Long userId, String state, Pageable pageable);

    List<AnswerBookingDto> getAllBookingByOwner(Long userId, String state, Pageable pageable);

}