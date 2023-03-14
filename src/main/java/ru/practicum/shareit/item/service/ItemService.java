package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.AnswerItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    AnswerItemDto getItem(Long itemId, Long userId);

    List<AnswerItemDto> getItemsByUser(Long userId);

    List<ItemDto> getAvailableItems(Long userId, String text);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}