package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.AnswerBookingDto;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.AnswerItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AnswerItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    default Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    @Mapping(target = "requestId", source = "request.id")
    ItemDto toItemDto(Item item);

    default Item updatedItem(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName() == null ? item.getName() : itemDto.getName())
                .description(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription())
                .available(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable())
                .build();
    }

    default AnswerItemDto toAnswerItemDto(Item item, InfoBookingDto lastBooking,
                                          InfoBookingDto nextBooking, List<CommentDto> comments) {
        return AnswerItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(toUserDto(item.getOwner()))
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    default User updatedUser(UserDto userDto, User user) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName() == null ? user.getName() : userDto.getName())
                .email(userDto.getEmail() == null ? user.getEmail() : userDto.getEmail())
                .build();
    }

    Booking toBooking(BookingDto bookingDto);

    @Mapping(target = "bookerId", source = "booker.id")
    InfoBookingDto toInfoBookingDto(Booking booking);

    default AnswerBookingDto toAnswerBookingDto(Booking booking) {
        return AnswerBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .booker(UserDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

    default AnswerItemRequestDto toAnswerItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        return AnswerItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items.stream().map(this::toItemDto).collect(Collectors.toList()))
                .build();
    }

    default ItemRequest toItemRequest(ItemRequestDto request, User requester) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

}