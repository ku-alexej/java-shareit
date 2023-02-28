package ru.practicum.shareit.item.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
public class InMemoryItemStorage implements ItemStorage {

    private Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item createItem(User user, Item item) {
        item.setId(id++);
        item.setOwner(user);
        items.put(item.getId(), item);
        log.info("New item got ID: {}, owner is user ID {}", item.getId(), user.getId());
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item, Long userId) {
        checkItemInBase(itemId);
        checkItemsOwner(itemId, userId);
        if (item.getName() != null) {
            items.get(itemId).setName(item.getName());
        }
        if (item.getDescription() != null) {
            items.get(itemId).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            items.get(itemId).setAvailable(item.getAvailable());
        }
        log.info("Item ID {} was updated by user ID {}", itemId, userId);
        return items.get(itemId);
    }

    @Override
    public Item getItem(Long itemId) {
        checkItemInBase(itemId);
        log.info("Item with ID {} was founded", itemId);
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUser(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAvailableItems(Long userId, String text) {
        String textToLower = text.toLowerCase();
        List<Item> itemsForUser = new ArrayList<>();
        if (!text.isBlank()) {
            itemsForUser = items.values()
                    .stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(textToLower)
                            || item.getDescription().toLowerCase().contains(textToLower))
                    .collect(Collectors.toList());
        }
        return itemsForUser;
    }

    private void checkItemInBase(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new EntityNotFoundException("Item with ID " + itemId + " does not exist");
        }
    }

    private void checkItemsOwner(Long itemId, Long userId) {
        if (!items.get(itemId).getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Item with ID " + itemId + " don't belong to user ID " + userId);
        }
    }

}