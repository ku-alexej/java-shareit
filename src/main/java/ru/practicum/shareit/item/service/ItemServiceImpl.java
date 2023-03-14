package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) throws EntityNotFoundException {
        UserDto owner = userService.getUser(userId);
        itemDto.setOwner(mapper.toUser(owner));
        return mapper.toItemDto(itemRepository.save(mapper.toItem(itemDto)));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto newItemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " does not exist"));
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item with ID " + itemId + " does not exist"));
        if (oldItem.getOwner().getId().equals(owner.getId())) {
            newItemDto.setId(itemId);
            Item item = mapper.updatedItem(newItemDto, oldItem);
            item.setOwner(owner);
            itemRepository.save(item);
            log.info("Item ID {} was updated by user ID {}", itemId, userId);
            return mapper.toItemDto(item);
        } else {
            throw new EntityNotFoundException("Item with ID " + itemId + " doesn't belong to user ID " + userId);
        }
    }

    @Override
    public AnswerItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item with ID " + itemId + " does not exist"));
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        LocalDateTime now = LocalDateTime.now();
        InfoBookingDto lastBooking;
        InfoBookingDto nextBooking;
        List<CommentDto> comments = commentRepository.findAllByItem_Id(item.getId()).stream()
                .map(mapper::toCommentDto)
                .collect(Collectors.toList());
        if (comments.isEmpty()) {
            comments = Collections.emptyList();
        }
        if (bookings.isEmpty()) {
            lastBooking = null;
            nextBooking = null;
        } else {
            lastBooking = mapper.toInfoBookingDto(bookingRepository.findLastBooking(item.getId(), userId, now)
                    .stream().findFirst().orElse(null));
            nextBooking = mapper.toInfoBookingDto(bookingRepository.findNextBooking(item.getId(), userId, now)
                    .stream().findFirst().orElse(null));
        }
        return mapper.toAnswerItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<AnswerItemDto> getItemsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " does not exist"));
        List<ItemDto> items = itemRepository.findByOwner_Id(userId);

        List<AnswerItemDto> list = new ArrayList<>();
        for (ItemDto item : items) {
            AnswerItemDto itemResponseDto = getItem(item.getId(), userId);
            list.add(itemResponseDto);
        }
        return list;
    }

    @Override
    public List<ItemDto> getAvailableItems(Long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.searchAvailableItems("%" + text + "%").stream()
                    .map(mapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item with ID " + itemId + " does not exist"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " does not exist"));
        if (bookingRepository.isItemWasUsedByUser(itemId, userId, LocalDateTime.now())) {
            Comment comment = new Comment(commentDto.getId(), commentDto.getText(), item, user, LocalDateTime.now());
            return mapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new EntityNotAvailable("User with ID " + userId + " has not finished renting item ID " + itemId);
        }
    }

}