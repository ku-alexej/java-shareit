package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.AnswerBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    User user;
    User owner;
    Item item;
    UserDto userDto;
    ItemDto itemDto;
    AnswerBookingDto answerBookingDto;
    BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user", "user@ya.ru");
        owner = new User(2L, "owner", "owner@ya.ru");
        item = new Item(1L, "item", "desc", true, owner, null);
        userDto = new UserDto(1L, "user", "user@ya.ru");
        itemDto = new ItemDto(1L, "item", "desc", true, owner, null);
        answerBookingDto = new AnswerBookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                itemDto,
                userDto,
                Status.WAITING);
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                itemDto.getId(),
                userDto.getId(),
                Status.WAITING);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(answerBookingDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(answerBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(answerBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(answerBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(answerBookingDto.getStatus().toString())));
    }

    @Test
    void createBooking_startInPast() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusDays(5));

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmationBooking() throws Exception {
        answerBookingDto.setStatus(Status.APPROVED);
        when(bookingService.confirmationBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(answerBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(answerBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(answerBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(answerBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(answerBookingDto.getStatus().toString())));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(answerBookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(answerBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(answerBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(answerBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(answerBookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingByUser() throws Exception {
        when(bookingService.getAllBookingByUser(anyLong(), any(), any())).thenReturn(List.of(answerBookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(answerBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(answerBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(answerBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(answerBookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingByOwner() throws Exception {
        when(bookingService.getAllBookingByOwner(anyLong(), any(), any())).thenReturn(List.of(answerBookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(answerBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(answerBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(answerBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(answerBookingDto.getStatus().toString())));
    }

}