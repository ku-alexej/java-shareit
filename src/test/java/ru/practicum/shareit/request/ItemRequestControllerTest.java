package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.AnswerItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    User user;

    AnswerItemRequestDto answerItemRequestDto;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user1", "user1@ya.ru");
        answerItemRequestDto = new AnswerItemRequestDto(
                1L,
                "description1",
                LocalDateTime.of(2023, 4, 10, 10, 10, 10),
                null);
        itemRequestDto = new ItemRequestDto(
                answerItemRequestDto.getId(),
                answerItemRequestDto.getDescription(),
                null,
                LocalDateTime.of(2023, 4, 10, 10, 10, 10));
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenReturn(answerItemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(answerItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(answerItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(answerItemRequestDto.getCreated().toString())));
    }

    @Test
    void createItemRequest_WithEmptyDescription() throws Exception {
        itemRequestDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemRequests() throws Exception {
        when(itemRequestService.getUsersItemRequests(anyLong()))
                .thenReturn(List.of(answerItemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(answerItemRequestDto))));
    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(answerItemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(answerItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(answerItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(answerItemRequestDto.getCreated().toString())));
    }

    @Test
    void getAllRequest() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(),any()))
                .thenReturn(List.of(answerItemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(answerItemRequestDto))))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getAllRequest_withPagination() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(),any()))
                .thenReturn(List.of(answerItemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(answerItemRequestDto))))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getAllRequest_withWrongFrom() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(),any()))
                .thenReturn(List.of(answerItemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-5")
                        .param("size", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRequest_withWrongSize() throws Exception {
        when(itemRequestService.getItemRequests(anyLong(),any()))
                .thenReturn(List.of(answerItemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

}