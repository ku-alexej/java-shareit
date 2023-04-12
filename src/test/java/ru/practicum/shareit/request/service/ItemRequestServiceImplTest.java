package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.EntityMapper;
import ru.practicum.shareit.request.dto.AnswerItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor
class ItemRequestServiceImplTest {

    ItemRequestService itemRequestService;

    @Autowired
    EntityMapper mapper;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    User user;
    Item item;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    List<Item> listItems;
    List<ItemRequest> listRequests;
    List<ItemRequestDto> listRequestsDto;

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository, mapper);
        user = new User(1L, "name", "user@ya.ru");
        itemRequest = new ItemRequest(
                1L,
                "d2",
                user,
                LocalDateTime.of(2022, 12, 12, 12, 12, 12));
        item = new Item(1L, "item", "d1", true, user, itemRequest);
        itemRequestDto = new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                user.getId(),
                itemRequest.getCreated());
        listItems = List.of(item);
        listRequests = List.of(itemRequest);
        listRequestsDto = List.of(itemRequestDto);

    }

    @Test
    void createItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        AnswerItemRequestDto res = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        assertNotNull(res);
        assertEquals(AnswerItemRequestDto.class, res.getClass());
        assertEquals(itemRequest.getId(), res.getId());
        assertEquals(itemRequest.getDescription(), res.getDescription());
        assertEquals(itemRequest.getCreated(), res.getCreated());
    }

    @Test
    void createItemRequest_WithWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.createItemRequest(999L, itemRequestDto));
    }

    @Test
    void getUsersItemRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(listRequests);
        when(itemRepository.findAllByRequesterId(anyLong())).thenReturn(listItems);

        List<AnswerItemRequestDto> res = itemRequestService.getUsersItemRequests(1L);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(AnswerItemRequestDto.class, res.get(0).getClass());
        assertEquals(itemRequest.getId(), res.get(0).getId());
        assertEquals(itemRequest.getDescription(), res.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), res.get(0).getCreated());
    }

    @Test
    void getUsersItemRequests_WithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getUsersItemRequests(999L));
    }

    @Test
    void getItemRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findRequestsWithoutOwner(anyLong(), any())).thenReturn(listRequests);
        when(itemRepository.findAllByRequesterId(anyLong())).thenReturn(listItems);

        List<AnswerItemRequestDto> res = itemRequestService.getItemRequests(1L, 5, 10);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(AnswerItemRequestDto.class, res.get(0).getClass());
        assertEquals(itemRequest.getId(), res.get(0).getId());
        assertEquals(itemRequest.getDescription(), res.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), res.get(0).getCreated());
    }

    @Test
    void getItemRequests_WithWrongSize() {
        assertThrows(EntityNotAvailable.class,
                () -> itemRequestService.getItemRequests(1L, 0, 10));
    }

    @Test
    void getItemRequests_WithWrongFrom() {
        assertThrows(EntityNotAvailable.class,
                () -> itemRequestService.getItemRequests(1L, 5, -1));
    }

    @Test
    void getItemRequests_WithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getItemRequests(999L, 5, 10));
    }

    @Test
    void getItemRequestById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequesterId(anyLong())).thenReturn(listItems);

        AnswerItemRequestDto res = itemRequestService.getItemRequestById(itemRequest.getId(), user.getId());

        assertNotNull(res);
        assertEquals(AnswerItemRequestDto.class, res.getClass());
        assertEquals(1, res.getItems().size());
        assertEquals(itemRequest.getId(), res.getId());
        assertEquals(itemRequest.getDescription(), res.getDescription());
        assertEquals(itemRequest.getCreated(), res.getCreated());
    }

    @Test
    void getItemRequestById_WithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getItemRequestById(itemRequest.getId(), 999L));
    }

}