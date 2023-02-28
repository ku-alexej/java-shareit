package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;

@Data
@Slf4j
public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        log.info("DTO to item");
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                null
        );
    }

    public static ItemDto toItemDto(Item item) {
        log.info("Item to DTO");
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                // Поле на данном этапе не нужно, но приведено в советах ментора к ТЗ 13 спринта
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

}