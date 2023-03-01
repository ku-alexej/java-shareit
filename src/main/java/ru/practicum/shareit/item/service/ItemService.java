package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(UserDto userDto, ItemDto itemDto);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> getItemsByUser(Long userId);

    List<ItemDto> getAvailableItems(Long userId, String text);

}