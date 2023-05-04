package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.AnswerItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.EntityMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.Status.APPROVED;

@SpringBootTest
@RequiredArgsConstructor
class ItemServiceImplTest {

    ItemService itemService;

    @Autowired
    UserService userService;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @Autowired
    EntityMapper mapper;

    User user1;
    User user2;
    UserDto userDto1;
    Item item1;
    Item item2;
    ItemDto itemDto1;
    ItemRequest request1;
    Booking booking1;
    Booking booking2;
    Pageable pageable;

    @BeforeEach
    void beforeEach() {
        pageable =  PageRequest.of(0, 10);
        itemService = new ItemServiceImpl(userService, bookingRepository, commentRepository,
                itemRepository, userRepository, itemRequestRepository, mapper);
        user1 = new User(1L, "user1", "mail1@ya.ru");
        user2 = new User(2L, "user2", "mail2@ya.ru");
        userDto1 = new UserDto(1L, "user1", "mail1@ya.ru");
        request1 = new ItemRequest(2L, "req2", user2, LocalDateTime.now());
        item1 = new Item(1L, "item1", "des1", true, user1, request1);
        item2 = new Item(2L, "item2", "des2", true, user1, null);
        itemDto1 = new ItemDto(1L, "item1", "des1", true, user1, request1.getId());
        booking1 = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item1, user2, APPROVED);
        booking2 = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item1, user2, APPROVED);

    }

    @Test
    void createItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request1));
        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto res = itemService.createItem(user1.getId(), itemDto1);

        assertNotNull(res);
        assertEquals(ItemDto.class, res.getClass());
        assertEquals(res.getId(), itemDto1.getId());
        assertEquals(res.getName(), itemDto1.getName());
        assertEquals(res.getDescription(), itemDto1.getDescription());
        assertEquals(res.getAvailable(), itemDto1.getAvailable());
        assertEquals(res.getOwner().toString(), itemDto1.getOwner().toString());
        assertEquals(res.getRequestId(), itemDto1.getRequestId());
    }

    @Test
    void createItem_withWrongItemRequestId() {
        itemDto1.setRequestId(99L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(user1.getId(), itemDto1));
    }

    @Test
    void updateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto newItemDto = new ItemDto(null, "upd", "upd", false, user1, request1.getId());

        ItemDto res = itemService.updateItem(itemDto1.getId(), newItemDto, user1.getId());

        assertNotNull(res);
        assertEquals(ItemDto.class, res.getClass());
        assertEquals(res.getId(), itemDto1.getId());
        assertEquals(res.getName(), newItemDto.getName());
        assertEquals(res.getDescription(), newItemDto.getDescription());
        assertEquals(res.getAvailable(), newItemDto.getAvailable());
        assertEquals(res.getOwner().toString(), itemDto1.getOwner().toString());
    }

    @Test
    void updateItem_itemDoesNotBelongToUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        ItemDto newItemDto = new ItemDto(null, "upd", "upd", false, user1, request1.getId());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(itemDto1.getId(), newItemDto, user1.getId()));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItem_Id((anyLong()))).thenReturn(List.of());
        when(bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndStartIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(booking1);
        when(bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot(
                anyLong(), anyLong(), any(), any(), any(), any())).thenReturn(booking2);

        AnswerItemDto res = itemService.getItem(itemDto1.getId(), user1.getId());

        assertNotNull(res);
        assertEquals(AnswerItemDto.class, res.getClass());
        assertEquals(res.getId(), itemDto1.getId());
        assertEquals(res.getName(), itemDto1.getName());
        assertEquals(res.getDescription(), itemDto1.getDescription());
        assertEquals(res.getAvailable(), itemDto1.getAvailable());
        assertEquals(res.getOwner().toString(), userDto1.toString());
        assertEquals(res.getLastBooking().toString(), mapper.toInfoBookingDto(booking1).toString());
        assertEquals(res.getNextBooking().toString(), mapper.toInfoBookingDto(booking2).toString());
        assertEquals(res.getComments().size(), 0);
    }

    @Test
    void getItemsByUser() {
        List<Item> items = List.of(item1, item2);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwner_IdOrderById(anyLong(), any())).thenReturn(items);
        when(commentRepository.findAllByItemsId(any())).thenReturn(List.of());
        when(bookingRepository.findFirstByItem_IdInAndItem_Owner_IdAndStartIsBefore(any(), anyLong(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findFirstByItem_IdInAndItem_Owner_IdAndStartIsAfterAndStatusIsNotAndStatusIsNot(
                any(), anyLong(), any(), any(), any(), any())).thenReturn(List.of(booking2));

        List<AnswerItemDto> res = itemService.getItemsByUser(user1.getId(), pageable);

        assertEquals(res.size(), 2);

        assertEquals(AnswerItemDto.class, res.get(0).getClass());
        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), mapper.toUserDto(user1).toString());
        assertEquals(res.get(0).getRequestId(), item1.getRequest().getId());
        assertEquals(res.get(0).getLastBooking().toString(), mapper.toInfoBookingDto(booking1).toString());
        assertEquals(res.get(0).getNextBooking().toString(), mapper.toInfoBookingDto(booking2).toString());
        assertEquals(res.get(0).getComments().size(), 0);

        assertEquals(AnswerItemDto.class, res.get(1).getClass());
        assertEquals(res.get(1).getId(), item2.getId());
        assertEquals(res.get(1).getName(), item2.getName());
        assertEquals(res.get(1).getDescription(), item2.getDescription());
        assertEquals(res.get(1).getAvailable(), item2.getAvailable());
        assertEquals(res.get(1).getOwner().toString(), mapper.toUserDto(user1).toString());
        assertNull(res.get(1).getRequestId());
        assertNull(res.get(1).getLastBooking());
        assertNull(res.get(1).getNextBooking());
        assertEquals(res.get(1).getComments().size(), 0);
    }

    @Test
    void getItemsByUser_wrongUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemsByUser(99L, pageable));
    }

    @Test
    void getAvailableItems() {
        when(itemRepository.searchAvailableItems(anyString(), any())).thenReturn(List.of(item1, item2));

        List<ItemDto> res = itemService.getAvailableItems(user1.getId(), "item", pageable);

        assertEquals(res.size(), 2);

        assertEquals(ItemDto.class, res.get(0).getClass());
        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), user1.toString());
        assertEquals(res.get(0).getRequestId(), item1.getRequest().getId());

        assertEquals(ItemDto.class, res.get(1).getClass());
        assertEquals(res.get(1).getId(), item2.getId());
        assertEquals(res.get(1).getName(), item2.getName());
        assertEquals(res.get(1).getDescription(), item2.getDescription());
        assertEquals(res.get(1).getAvailable(), item2.getAvailable());
        assertEquals(res.get(1).getOwner().toString(), user1.toString());
        assertNull(res.get(1).getRequestId());
    }

    @Test
    void getAvailableItems_withBlankText() {
        List<ItemDto> res = itemService.getAvailableItems(user1.getId(), "       ", pageable);

        assertEquals(res.size(), 0);
    }

    @Test
    void createComment() {
        Comment comment = new Comment(1L, "comment1", item1, user2, LocalDateTime.now());
        CommentDto commentDto = new CommentDto(1L, "comment1", user2.getName(), comment.getCreated());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.isItemWasUsedByUser(anyLong(), anyLong(), any())).thenReturn(true);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto res = itemService.createComment(item1.getId(), user1.getId(), commentDto);

        assertNotNull(res);
        assertEquals(CommentDto.class, res.getClass());
        assertEquals(res.getId(), commentDto.getId());
        assertEquals(res.getText(), commentDto.getText());
        assertEquals(res.getAuthorName(), commentDto.getAuthorName());
        assertEquals(res.getCreated().toString(), commentDto.getCreated().toString());
    }

    @Test
    void createComment_whenUserDoesNotUseItem() {
        Comment comment = new Comment(1L, "comment1", item1, user2, LocalDateTime.now());
        CommentDto commentDto = new CommentDto(1L, "comment1", user2.getName(), comment.getCreated());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.isItemWasUsedByUser(anyLong(), anyLong(), any())).thenReturn(false);

        assertThrows(EntityNotAvailable.class,
                () -> itemService.createComment(item1.getId(), user1.getId(), commentDto));
    }

}