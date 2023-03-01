package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

}