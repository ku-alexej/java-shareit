package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.AnswerBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void createBooking() {
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setOwner(savedOwner);
        item.setAvailable(true);
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        AnswerBookingDto result = bookingService.createBooking(booker.getId(), bookingDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getBooker());
        assertNotNull(result.getBooker().getId());
        assertNotNull(result.getItem());
        assertNotNull(result.getItem().getId());
        assertEquals(result.getStart(), bookingDto.getStart());
        assertEquals(result.getEnd(), bookingDto.getEnd());
    }

    @Test
    void createBooking_itemUnavailable() {
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setOwner(savedOwner);
        item.setAvailable(false);
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        assertThrows(EntityNotAvailable.class, () -> bookingService.createBooking(booker.getId(), bookingDto));
    }

    @Test
    void createBooking_startAfterEnd() {
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setOwner(savedOwner);
        item.setAvailable(true);
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));

        assertThrows(EntityNotAvailable.class, () -> bookingService.createBooking(booker.getId(), bookingDto));
    }

    @Test
    void createBooking_bookerIsOwner() {
        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setOwner(savedOwner);
        item.setAvailable(true);
        Item savedItem = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(owner.getId(), bookingDto));
    }

    @Test
    void confirmationBooking_updateStatusToApproved() {
        LocalDateTime now = LocalDateTime.now();
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        AnswerBookingDto updatedBooking = bookingService
                .confirmationBooking(savedOwner.getId(), savedBooking.getId(), true);

        assertEquals(updatedBooking.getStatus(), Status.APPROVED);
    }

    @Test
    void confirmationBooking_updateStatusToRejected() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        AnswerBookingDto updatedBooking = bookingService
                .confirmationBooking(savedOwner.getId(), savedBooking.getId(), false);

        assertEquals(updatedBooking.getStatus(), Status.REJECTED);
    }


    @Test
    void confirmationBooking_wrongBookingId() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        bookingRepository.save(booking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.confirmationBooking(savedOwner.getId(), 99L, false));
    }

    @Test
    void confirmationBooking_notByOwner() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.confirmationBooking(savedBooker.getId(), savedBooking.getId(), false));
    }

    @Test
    void confirmationBooking_ifStatusConfirmed() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        assertThrows(EntityNotAvailable.class,
                () -> bookingService.confirmationBooking(savedOwner.getId(), savedBooking.getId(), false));
    }

    @Test
    void getBooking() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking savedBooking = bookingRepository.save(booking);

        AnswerBookingDto result = bookingService.getBooking(savedBooker.getId(), savedBooking.getId());

        assertNotNull(result);
        assertEquals(result.getId(), savedBooking.getId());
    }

    @Test
    void getBooking_byThirdUser() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        bookingRepository.save(booking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(booking.getId(), 99L));
    }


    @Test
    void getAllBookingByUser() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User booker2 = new User();
        booker2.setName("booker2");
        booker2.setEmail("booker2@ya.ru");
        User savedBooker2 = userRepository.save(booker2);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking sBooking = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusHours(3));
        booking2.setEnd(now.plusHours(4));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(savedItem);
        booking2.setBooker(savedBooker2);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(now.plusHours(6));
        booking3.setEnd(now.plusHours(10));
        booking3.setStatus(Status.APPROVED);
        booking3.setItem(savedItem);
        booking3.setBooker(savedBooker);
        Booking sBooking3 = bookingRepository.save(booking3);

        List<AnswerBookingDto> actualBookingsUser = bookingService
                .getAllBookingByUser(savedBooker.getId(), "ALL", 0, 10);

        assertNotNull(actualBookingsUser);
        assertEquals(actualBookingsUser.size(), 2);
        assertEquals(actualBookingsUser.get(0).getId(), sBooking3.getId());
        assertEquals(actualBookingsUser.get(1).getId(), sBooking.getId());
    }

    @Test
    void getAllBookingByOwner() {
        LocalDateTime now = LocalDateTime.now();

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        User savedBooker = userRepository.save(booker);

        User booker2 = new User();
        booker2.setName("booker2");
        booker2.setEmail("booker2@ya.ru");
        User savedBooker2 = userRepository.save(booker2);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("desc2");
        item2.setAvailable(true);
        item2.setOwner(booker2);
        Item savedItem2 = itemRepository.save(item2);

        Booking booking = new Booking();
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        Booking sBooking = bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusHours(3));
        booking2.setEnd(now.plusHours(4));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(savedItem);
        booking2.setBooker(savedBooker2);
        Booking sBooking2 = bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(now.plusHours(6));
        booking3.setEnd(now.plusHours(10));
        booking3.setStatus(Status.APPROVED);
        booking3.setItem(savedItem2);
        booking3.setBooker(savedBooker);
        bookingRepository.save(booking3);

        List<AnswerBookingDto> actualBookingsUser = bookingService
                .getAllBookingByOwner(savedOwner.getId(), "ALL", 0, 10);

        assertNotNull(actualBookingsUser);
        assertEquals(actualBookingsUser.size(), 2);
        assertEquals(actualBookingsUser.get(0).getId(), sBooking2.getId());
        assertEquals(actualBookingsUser.get(1).getId(), sBooking.getId());
    }

}