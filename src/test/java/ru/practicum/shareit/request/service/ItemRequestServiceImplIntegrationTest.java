package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.AnswerItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createItemRequest() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("desc");

        AnswerItemRequestDto res = itemRequestService.createItemRequest(savedUser.getId(), requestDto);

        assertNotNull(res);
        assertEquals(res.getDescription(), requestDto.getDescription());
        assertNotNull(res.getCreated());
        assertEquals(res.getItems().size(), 0);
    }

    @Test
    void getUsersItemRequests() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("desc");
        itemRequestService.createItemRequest(savedUser.getId(), requestDto);

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("desc2");
        itemRequestService.createItemRequest(savedUser.getId(), requestDto2);

        List<AnswerItemRequestDto> res = itemRequestService.getUsersItemRequests(savedUser.getId());

        assertNotNull(res);
        assertEquals(res.size(), 2);
        assertEquals(res.get(0).getDescription(), requestDto2.getDescription());
        assertEquals(res.get(1).getDescription(), requestDto.getDescription());
    }

    @Test
    void getItemRequests() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        User user2 = new User();
        user2.setName("Max");
        user2.setEmail("max@ya.ru");
        User savedUser2 = userRepository.save(user2);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("desc");
        itemRequestService.createItemRequest(savedUser.getId(), requestDto);

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("desc2");
        itemRequestService.createItemRequest(savedUser2.getId(), requestDto2);

        List<AnswerItemRequestDto> res = itemRequestService.getItemRequests(savedUser.getId(), pageable);

        assertNotNull(res);
        assertEquals(res.size(), 1);
        assertEquals(res.get(0).getDescription(), requestDto2.getDescription());
    }

    @Test
    void getItemRequestById() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        User savedUser = userRepository.save(user);

        User user2 = new User();
        user2.setName("Max");
        user2.setEmail("max@ya.ru");
        User savedUser2 = userRepository.save(user2);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("desc");
        AnswerItemRequestDto savedItemRequest = itemRequestService.createItemRequest(savedUser.getId(), requestDto);

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("desc2");
        itemRequestService.createItemRequest(savedUser2.getId(), requestDto2);

        AnswerItemRequestDto res = itemRequestService.getItemRequestById(savedItemRequest.getId(), savedUser2.getId());

        assertNotNull(res);
        assertEquals(res.getDescription(), requestDto.getDescription());
    }
}