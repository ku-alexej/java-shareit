package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    User requester1;
    User requester2;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    ItemRequest itemRequest3;

    @BeforeAll
    private void beforeAll() {
        requester1 = new User(1L, "user1", "mail1@ya.ru");
        requester2 = new User(2L, "user2", "mail2@ya.ru");
        userRepository.save(requester1);
        userRepository.save(requester2);
        itemRequest1 = new ItemRequest(1L, "req1", requester1, LocalDateTime.now());
        itemRequest2 = new ItemRequest(2L, "req2", requester2, LocalDateTime.now());
        itemRequest3 = new ItemRequest(3L, "req3", requester1, LocalDateTime.now());
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
        itemRequestRepository.save(itemRequest3);
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> res = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(requester1.getId());

        assertEquals(res.size(), 2);
        assertEquals(itemRequest3.getId(), res.get(0).getId());
        assertEquals(itemRequest3.getDescription(), res.get(0).getDescription());
        assertEquals(itemRequest3.getRequester().getId(), res.get(0).getRequester().getId());
        assertEquals(itemRequest1.getId(), res.get(1).getId());
        assertEquals(itemRequest1.getDescription(), res.get(1).getDescription());
        assertEquals(itemRequest1.getRequester().getId(), res.get(1).getRequester().getId());
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc_userWithoutRequests() {
        List<ItemRequest> res = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(999L);

        assertEquals(res.size(), 0);
    }

    @Test
    void findRequestsWithoutOwner() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ItemRequest> res = itemRequestRepository
                .findRequestsWithoutOwner(requester2.getId(), pageable);

        assertEquals(res.size(), 2);
        assertEquals(itemRequest3.getId(), res.get(0).getId());
        assertEquals(itemRequest3.getDescription(), res.get(0).getDescription());
        assertEquals(itemRequest3.getRequester().getId(), res.get(0).getRequester().getId());
        assertEquals(itemRequest1.getId(), res.get(1).getId());
        assertEquals(itemRequest1.getDescription(), res.get(1).getDescription());
        assertEquals(itemRequest1.getRequester().getId(), res.get(1).getRequester().getId());
    }

    @Test
    void findRequestsWithoutOwner_userWithoutRequests() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ItemRequest> res = itemRequestRepository
                .findRequestsWithoutOwner(999L, pageable);

        assertEquals(res.size(), 3);
        assertEquals(itemRequest3.getId(), res.get(0).getId());
        assertEquals(itemRequest3.getDescription(), res.get(0).getDescription());
        assertEquals(itemRequest3.getRequester().getId(), res.get(0).getRequester().getId());
        assertEquals(itemRequest2.getId(), res.get(1).getId());
        assertEquals(itemRequest2.getDescription(), res.get(1).getDescription());
        assertEquals(itemRequest2.getRequester().getId(), res.get(1).getRequester().getId());
        assertEquals(itemRequest1.getId(), res.get(2).getId());
        assertEquals(itemRequest1.getDescription(), res.get(2).getDescription());
        assertEquals(itemRequest1.getRequester().getId(), res.get(2).getRequester().getId());
    }
}