package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createItem() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("desc");
        itemDto.setAvailable(true);

        ItemDto savedItem = itemService.createItem(savedUser.getId(), itemDto);

        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), savedItem.getName());
        assertEquals(itemDto.getDescription(), savedItem.getDescription());
        assertEquals(itemDto.getAvailable(), savedItem.getAvailable());

    }

    @Test
    void createItem_invalidItemRequest() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("desc");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        assertThrows(EntityNotFoundException.class, () -> itemService.createItem(savedUser.getId(), itemDto));
    }

    @Test
    void updateItem() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        Item oldItem = new Item();
        oldItem.setName("item");
        oldItem.setDescription("desc");
        oldItem.setAvailable(true);
        oldItem.setOwner(savedUser);
        itemRepository.save(oldItem);

        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("newItem");

        ItemDto updatedItemDto = itemService.updateItem(oldItem.getId(), newItemDto, user.getId());

        assertEquals(updatedItemDto.getName(), newItemDto.getName());
        assertEquals(updatedItemDto.getDescription(), oldItem.getDescription());
        assertEquals(updatedItemDto.getAvailable(), oldItem.getAvailable());
    }

    @Test
    void updateItem_byWrongUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        Item oldItem = new Item();
        oldItem.setName("item");
        oldItem.setDescription("desc");
        oldItem.setAvailable(true);
        oldItem.setOwner(savedUser);
        itemRepository.save(oldItem);

        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("update");

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(oldItem.getId(), itemDto, 99L));
    }

    @Test
    void updateItem_wrongItemId() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        Item oldItem = new Item();
        oldItem.setName("item");
        oldItem.setDescription("desc");
        oldItem.setAvailable(true);
        oldItem.setOwner(savedUser);
        itemRepository.save(oldItem);

        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("update");

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(99L, itemDto, savedUser.getId()));
    }

    @Test
    void getItem() {
        LocalDateTime now = LocalDateTime.now();
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        userRepository.save(booker);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        userRepository.save(owner);

        Item item = new Item();
        item.setName("item");
        item.setOwner(owner);
        item.setDescription("desc");
        item.setAvailable(true);
        itemRepository.save(item);

        Booking lastBooking = new Booking();
        lastBooking.setBooker(booker);
        lastBooking.setStart(now.minusDays(2));
        lastBooking.setEnd(now.minusDays(1));
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setItem(item);
        bookingRepository.save(lastBooking);

        Booking nextBooking = new Booking();
        nextBooking.setBooker(booker);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setEnd(now.plusDays(2));
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setItem(item);
        bookingRepository.save(nextBooking);

        AnswerItemDto answerItemDto = itemService.getItem(item.getId(), owner.getId());

        assertNotNull(answerItemDto);
        assertEquals(answerItemDto.getName(), item.getName());
        assertEquals(answerItemDto.getDescription(), item.getDescription());
        assertEquals(answerItemDto.getAvailable(), item.getAvailable());
        assertEquals(answerItemDto.getLastBooking().getStart(), lastBooking.getStart());
        assertEquals(answerItemDto.getNextBooking().getStart(), nextBooking.getStart());
    }

    @Test
    void getItemsByUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("desc1");
        item1.setAvailable(true);
        item1.setOwner(savedUser);
        Item savedItem1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("desc2");
        item2.setAvailable(true);
        item2.setOwner(savedUser);
        itemRepository.save(item2);

        Comment comment = new Comment();
        comment.setAuthor(savedUser);
        comment.setText("comment");
        comment.setItem(savedItem1);
        commentRepository.save(comment);

        List<AnswerItemDto> items = itemService.getItemsByUser(user.getId(), 0, 10);

        assertNotNull(items);
        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getName(), item1.getName());
        assertEquals(items.get(0).getComments().get(0).getText(), comment.getText());
        assertEquals(items.get(1).getName(), item2.getName());
        assertEquals(items.get(1).getComments().size(), 0);
    }

    @Test
    void getItemsByUser_wrongDataForPagination() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("desc1");
        item1.setAvailable(true);
        item1.setOwner(savedUser);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("desc2");
        item2.setAvailable(true);
        item2.setOwner(savedUser);
        itemRepository.save(item2);

        assertThrows(EntityNotAvailable.class, () -> itemService.getItemsByUser(user.getId(), -1, 0));
    }

    @Test
    void getAvailableItems() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("desc1");
        item1.setAvailable(true);
        item1.setOwner(savedUser);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("item2-search");
        item2.setDescription("desc2");
        item2.setAvailable(true);
        item2.setOwner(savedUser);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("item3-search");
        item3.setDescription("desc3");
        item3.setAvailable(false);
        item3.setOwner(savedUser);
        itemRepository.save(item3);

        List<ItemDto> items = itemService.getAvailableItems(user.getId(), "sea", 0, 10);

        assertNotNull(items);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), item2.getName());
        assertEquals(items.get(0).getAvailable(), true);
    }

    @Test
    void createComment() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        User owner = new User();
        owner.setName("Max");
        owner.setEmail("max@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item1");
        item.setDescription("desc1");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setBooker(savedUser);
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        booking.setStatus(Status.APPROVED);
        booking.setItem(savedItem);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        commentDto.setAuthorName(savedUser.getName());

        CommentDto savedComment = itemService.createComment(item.getId(), savedUser.getId(), commentDto);

        assertNotNull(savedComment);
        assertEquals(savedComment.getText(), commentDto.getText());
        assertEquals(savedComment.getAuthorName(), user.getName());
    }

    @Test
    public void createComment_fromInvalidUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        User owner = new User();
        owner.setName("Max");
        owner.setEmail("max@ya.ru");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("item1");
        item.setDescription("desc1");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        itemRepository.save(item);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        commentDto.setAuthorName(savedUser.getName());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(item.getId(), 99L, commentDto));
    }

}