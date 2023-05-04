package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests : user ID {} creates itemRequest from DTO - {}", userId, itemRequestDto);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests : get list of itemRequests by user ID {}", userId);
        return itemRequestClient.getUsersItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        log.info("GET /requests/all?from={}&size={} : get list of itemRequests, user ID {}", from, size, userId);
        return itemRequestClient.getItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable("requestId") Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/{} : get itemRequest by ID, user ID {}", requestId, userId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
