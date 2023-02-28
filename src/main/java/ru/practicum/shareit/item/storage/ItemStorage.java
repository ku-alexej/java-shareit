package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {

    Item createItem(User user, Item item);

    Item updateItem(Long itemId, Item item, Long userId);

    Item getItem(Long itemId);

    List<Item> getItemsByUser(Long userId);

    List<Item> getAvailableItems(Long userId, String text);

}