package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.AnswerBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.ServerError;
import ru.practicum.shareit.exceptions.UnsupportedState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.EntityMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final Sort SORT = Sort.by(Sort.Direction.DESC, "start");

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final EntityMapper mapper;


    @Override
    public AnswerBookingDto createBooking(Long userId, BookingDto bookingDto) {
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item with ID " + itemId + " does not exist"));
        if (!item.getAvailable()) {
            throw new EntityNotAvailable("Item with ID " + itemId + " isn't available");
        }
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new EntityNotAvailable("Booking's end time must be after start time");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new ServerError("User with ID " + userId + " does not exist"));
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new EntityNotFoundException("User with ID " + userId + " trying to book his/her own item");
        }
        Booking booking = mapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return mapper.toAnswerBookingDto(bookingRepository.save(booking));
    }

    @Override
    public AnswerBookingDto confirmationBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID " + bookingId + " does not exist"));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId())) {
            throw new EntityNotFoundException("User ID " + userId + " does not own item in booking ID " + bookingId);
        }
        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
            throw new EntityNotAvailable("Booking status already confirmed: " + booking.getStatus());
        }
        if (approved != null) {
            booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return mapper.toAnswerBookingDto(booking);
    }

    @Override
    public AnswerBookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID " + bookingId + " does not exist"));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new EntityNotFoundException("User ID " + userId + " can't get booking ID " + bookingId);
        }
        return mapper.toAnswerBookingDto(booking);
    }

    @Override
    public List<AnswerBookingDto> getAllBookingByUser(Long userId, String rawState) {
        State state = getState(rawState);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), SORT);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), SORT);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), SORT);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, Status.REJECTED);
                break;
        }

        return bookings.isEmpty() ? Collections.emptyList() : bookings.stream()
                .map(mapper::toAnswerBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnswerBookingDto> getAllBookingByOwner(Long userId, String rawState) {
        State state = getState(rawState);
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), SORT);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), SORT);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), SORT);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(userId, Status.REJECTED);
                break;
        }

        return bookings.stream()
                .map(mapper::toAnswerBookingDto)
                .collect(Collectors.toList());
    }


    private State getState(String rawState) {
        State state;
        try {
            state = State.valueOf(rawState);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedState("Unknown state: " + rawState);
        }
        return state;
    }

}