package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.EntityMapper;
import ru.practicum.shareit.request.dto.AnswerItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    public AnswerItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " does not exist"));
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto, user);
        return mapper.toAnswerItemRequestDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    public List<AnswerItemRequestDto> getUsersItemRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(req -> mapper.toAnswerItemRequestDto(req, itemRepository.findAllByRequesterId(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<AnswerItemRequestDto> getItemRequests(Long userId, int size, int from) {
        if (size < 1 || from < 0) {
            throw new EntityNotAvailable("Invalid \"size\" or \"from\"");
        }
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }
        int page = from / size;
        List<ItemRequest> requests = itemRequestRepository.findRequestsWithoutOwner(userId, PageRequest.of(page, size));
        return requests.stream()
                .map(request -> mapper.toAnswerItemRequestDto(request,
                        itemRepository.findAllByRequesterId(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public AnswerItemRequestDto getItemRequestById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("ItemRequest with ID " + requestId + " does not exist"));
        return mapper.toAnswerItemRequestDto(itemRequest, itemRepository.findAllByRequesterId(itemRequest.getId()));
    }

}
