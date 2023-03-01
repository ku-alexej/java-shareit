package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.mapper.EntityMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final EntityMapper mapper;

    @Override
    public ItemDto createItem(UserDto userDto, ItemDto itemDto) {
        return mapper.toItemDto(itemStorage.createItem(mapper.toUser(userDto), mapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        return mapper.toItemDto(itemStorage.updateItem(itemId, mapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        return mapper.toItemDto(itemStorage.getItem(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUser(Long userId) {
        return itemStorage.getItemsByUser(userId).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItems(Long userId, String text) {
        return itemStorage.getAvailableItems(userId, text).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }
}