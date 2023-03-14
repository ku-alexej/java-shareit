package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.AnswerBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public AnswerBookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        log.info("POST /bookings : create booking from DTO - {}", bookingDto);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public AnswerBookingDto confirmationBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("PATCH /bookings/{} : update booking's status by ID - approved {}, user ID {}",
                bookingId, approved, userId);
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public AnswerBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId) {
        log.info("GET /bookings/{} : get booking by ID by user ID {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<AnswerBookingDto> getAllBookingByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("GET /bookings?state={} : get list of bookings by user ID {} with state", state, userId);
        return bookingService.getAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<AnswerBookingDto> getAllBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("GET /bookings/owner?state={} : get list of bookings by owner ID {} with state", state, userId);
        return bookingService.getAllBookingByOwner(userId, state);
    }

}