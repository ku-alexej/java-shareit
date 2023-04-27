package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.AnswerItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    AnswerItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<AnswerItemRequestDto> getUsersItemRequests(Long userId);

    List<AnswerItemRequestDto> getItemRequests(Long userId, Pageable pageable);

    AnswerItemRequestDto getItemRequestById(Long requestId, Long userId);

}