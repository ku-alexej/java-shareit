package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.AnswerBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.EntityMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    BookingService bookingService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    BookingRepository bookingRepository;

    @Autowired
    EntityMapper mapper;


    User user;
    User owner;
    Item item;
    Booking booking;
    UserDto userDto;
    ItemDto itemDto;
    AnswerBookingDto answerBookingDto;
    BookingDto bookingDto;
    Pageable pageable;

    @BeforeEach
    void beforeEach() {
        pageable =  PageRequest.of(0, 10);
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository, mapper);
        user = new User(1L, "user", "user@ya.ru");
        owner = new User(2L, "owner", "owner@ya.ru");
        item = new Item(1L, "item", "desc", true, owner, null);
        userDto = new UserDto(1L, "user", "user@ya.ru");
        itemDto = new ItemDto(1L, "item", "desc", true, owner, null);
        booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                Status.WAITING);
        answerBookingDto = new AnswerBookingDto(
                1L,
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                userDto,
                Status.WAITING);
        bookingDto = new BookingDto(
                1L,
                booking.getStart(),
                booking.getEnd(),
                item.getId(),
                user.getId(),
                Status.WAITING);
    }

    @Test
    void createBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);

        AnswerBookingDto res = bookingService.createBooking(user.getId(), bookingDto);

        assertNotNull(res);
        assertEquals(AnswerBookingDto.class, res.getClass());
        assertEquals(res.getId(), answerBookingDto.getId());
        assertEquals(res.getStart(), answerBookingDto.getStart());
        assertEquals(res.getEnd(), answerBookingDto.getEnd());
        assertEquals(res.getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void createBooking_whenItemUnavailable() {
        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(EntityNotAvailable.class, () -> bookingService.createBooking(user.getId(), bookingDto));
    }

    @Test
    void createBooking_whenStartAfterEnd() {
        bookingDto.setStart(bookingDto.getEnd().plusDays(2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(EntityNotAvailable.class, () -> bookingService.createBooking(user.getId(), bookingDto));
    }

    @Test
    void createBooking_whenBookerIsOwner() {
        item.setOwner(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(user.getId(), bookingDto));
    }

    @Test
    void confirmationBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        AnswerBookingDto res = bookingService.confirmationBooking(owner.getId(), booking.getId(), true);
        answerBookingDto.setStatus(Status.APPROVED);

        assertNotNull(res);
        assertEquals(AnswerBookingDto.class, res.getClass());
        assertEquals(res.getId(), answerBookingDto.getId());
        assertEquals(res.getStart(), answerBookingDto.getStart());
        assertEquals(res.getEnd(), answerBookingDto.getEnd());
        assertEquals(res.getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void confirmationBooking_whenUserIsNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.confirmationBooking(99L, booking.getId(), true));
    }

    @Test
    void confirmationBooking_whenStatusConfirmed() {
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotAvailable.class,
                () -> bookingService.confirmationBooking(owner.getId(), booking.getId(), true));
    }

    @Test
    void getBooking_byOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        AnswerBookingDto res = bookingService.getBooking(owner.getId(), booking.getId());

        assertNotNull(res);
        assertEquals(AnswerBookingDto.class, res.getClass());
        assertEquals(res.getId(), answerBookingDto.getId());
        assertEquals(res.getStart(), answerBookingDto.getStart());
        assertEquals(res.getEnd(), answerBookingDto.getEnd());
        assertEquals(res.getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getBooking_byBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        AnswerBookingDto res = bookingService.getBooking(user.getId(), booking.getId());

        assertNotNull(res);
        assertEquals(AnswerBookingDto.class, res.getClass());
        assertEquals(res.getId(), answerBookingDto.getId());
        assertEquals(res.getStart(), answerBookingDto.getStart());
        assertEquals(res.getEnd(), answerBookingDto.getEnd());
        assertEquals(res.getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getBooking_byAnotherUser() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(5L, booking.getId()));
    }

    @Test
    void getAllBookingByUser_statusIsAll() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByUser(user.getId(), "ALL", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsPast() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        answerBookingDto.setStart(booking.getStart());
        answerBookingDto.setEnd(booking.getEnd());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByUser(user.getId(), "PAST", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsFuture() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByUser(user.getId(), "FUTURE", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        answerBookingDto.setStart(booking.getStart());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByUser(user.getId(), "CURRENT", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsWaiting() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByUser(user.getId(), "WAITING", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByUser_statusIsRejected() {
        booking.setStatus(Status.REJECTED);
        answerBookingDto.setStatus(Status.REJECTED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBooker_IdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByUser(user.getId(), "REJECTED", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByUser_wrongUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingByUser(owner.getId(), "ALL", pageable));
    }

    @Test
    void getAllBookingByOwner_statusIsAll() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByOwner(owner.getId(), "ALL", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsPast() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        answerBookingDto.setStart(booking.getStart());
        answerBookingDto.setEnd(booking.getEnd());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByOwner(owner.getId(), "PAST", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsFuture() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByOwner(owner.getId(), "FUTURE", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        answerBookingDto.setStart(booking.getStart());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByOwner(owner.getId(), "CURRENT", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsWaiting() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByOwner(owner.getId(), "WAITING", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_statusIsRejected() {
        booking.setStatus(Status.REJECTED);
        answerBookingDto.setStatus(Status.REJECTED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<AnswerBookingDto> res = bookingService.getAllBookingByOwner(owner.getId(), "REJECTED", pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), answerBookingDto.getId());
        assertEquals(res.get(0).getStart(), answerBookingDto.getStart());
        assertEquals(res.get(0).getEnd(), answerBookingDto.getEnd());
        assertEquals(res.get(0).getItem().getId(), answerBookingDto.getItem().getId());
        assertEquals(res.get(0).getBooker().getId(), answerBookingDto.getBooker().getId());
        assertEquals(res.get(0).getStatus(), answerBookingDto.getStatus());
    }

    @Test
    void getAllBookingByOwner_wrongUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingByOwner(owner.getId(), "ALL", pageable));
    }

    @Test
    void getAllBookingByOwner_wrongState() {
        assertThrows(UnsupportedState.class,
                () -> bookingService.getAllBookingByOwner(owner.getId(), "MEOW", pageable));
    }

}