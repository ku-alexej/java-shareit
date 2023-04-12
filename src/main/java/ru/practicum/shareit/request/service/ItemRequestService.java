package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.AnswerItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    AnswerItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<AnswerItemRequestDto> getUsersItemRequests(Long userId);

    List<AnswerItemRequestDto> getItemRequests(Long userId, int size, int from);

    AnswerItemRequestDto getItemRequestById(Long requestId, Long userId);

}