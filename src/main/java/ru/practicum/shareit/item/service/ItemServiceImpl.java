package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public ItemDto createItem(UserDto userDto, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemStorage.createItem(UserMapper.toUser(userDto), ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public Item getItem(Long itemId, Long userId) {
        return itemStorage.getItem(itemId);
    }

    @Override
    public List<Item> getItemsByUser(Long userId) {
        return itemStorage.getItemsByUser(userId);
    }

    @Override
    public List<Item> getAvailableItems(Long userId, String text) {
        return itemStorage.getAvailableItems(userId, text);
    }
}