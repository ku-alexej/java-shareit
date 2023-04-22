package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.EntityNotAvailable;
import ru.practicum.shareit.request.dto.AnswerItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public AnswerItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests : user ID {} creates itemRequest from DTO - {}", userId, itemRequestDto);
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<AnswerItemRequestDto> getUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests : get list of itemRequests by user ID {}", userId);
        return itemRequestService.getUsersItemRequests(userId);
    }

    @GetMapping("/all")
    public List<AnswerItemRequestDto> getItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        if (from < 0 || size < 1) {
            throw new EntityNotAvailable("Invalid \"size\" or \"from\"");
        }
        log.info("GET /requests/all?from={}&size={} : get list of itemRequests, user ID {}", from, size, userId);
        return itemRequestService.getItemRequests(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{requestId}")
    public AnswerItemRequestDto getItemRequest(@PathVariable("requestId") Long requestId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/{} : get itemRequest by ID, user ID {}", requestId, userId);
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
