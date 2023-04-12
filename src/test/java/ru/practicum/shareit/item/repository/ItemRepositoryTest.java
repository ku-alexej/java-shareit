package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    Item item1;
    Item item2;
    Item item3;
    ItemRequest request1;
    ItemRequest request2;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeAll
    public void beforeAll() {
        user1 = new User(1L, "user1", "mail1@ya.ru");
        user2 = new User(2L, "user2", "mail2@ya.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        request1 = new ItemRequest(1L, "req1", user1, LocalDateTime.now());
        request2 = new ItemRequest(2L, "req2", user2, LocalDateTime.now());
        itemRequestRepository.save(request1);
        itemRequestRepository.save(request2);
        item1 = new Item(1L, "itEm1", "caRrot", true, user1, request2);
        item2 = new Item(2L, "item2", "Meowem1 pot", true, user2, request1);
        item3 = new Item(3L, "item3", "carrotem1", false, user2, null);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @Test
    void findByOwner_Id() {
        List<Item> res = itemRepository.findByOwner_Id(user2.getId(), pageable);

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getId(), item2.getId());
        assertEquals(res.get(0).getName(), item2.getName());
        assertEquals(res.get(0).getDescription(), item2.getDescription());
        assertEquals(res.get(0).getAvailable(), item2.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item2.getOwner().toString());
        assertEquals(res.get(0).getRequest().toString(), item2.getRequest().toString());

        assertEquals(res.get(1).getId(), item3.getId());
        assertEquals(res.get(1).getName(), item3.getName());
        assertEquals(res.get(1).getDescription(), item3.getDescription());
        assertEquals(res.get(1).getAvailable(), item3.getAvailable());
        assertEquals(res.get(1).getOwner().toString(), item3.getOwner().toString());
        assertEquals(res.get(1).getRequest(), item3.getRequest());
    }

    @Test
    void searchAvailableItems_textInLowerCase() {
        List<Item> res = itemRepository.searchAvailableItems("%carrot%", pageable);

        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item1.getOwner().toString());
        assertEquals(res.get(0).getRequest().toString(), item1.getRequest().toString());
    }

    @Test
    void searchAvailableItems_textInUpperCase() {
        List<Item> res = itemRepository.searchAvailableItems("%CARROT%", pageable);

        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item1.getOwner().toString());
        assertEquals(res.get(0).getRequest().toString(), item1.getRequest().toString());
    }

    @Test
    void searchAvailableItems_textInMixCase() {
        List<Item> res = itemRepository.searchAvailableItems("%CaRRot%", pageable);

        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item1.getOwner().toString());
        assertEquals(res.get(0).getRequest().toString(), item1.getRequest().toString());
    }

    @Test
    void searchAvailableItems_textInNameInDescription() {
        List<Item> res = itemRepository.searchAvailableItems("%eM1%", pageable);

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getId(), item1.getId());
        assertEquals(res.get(0).getName(), item1.getName());
        assertEquals(res.get(0).getDescription(), item1.getDescription());
        assertEquals(res.get(0).getAvailable(), item1.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item1.getOwner().toString());
        assertEquals(res.get(0).getRequest().toString(), item1.getRequest().toString());

        assertEquals(res.get(1).getId(), item2.getId());
        assertEquals(res.get(1).getName(), item2.getName());
        assertEquals(res.get(1).getDescription(), item2.getDescription());
        assertEquals(res.get(1).getAvailable(), item2.getAvailable());
        assertEquals(res.get(1).getOwner().toString(), item2.getOwner().toString());
        assertEquals(res.get(1).getRequest().toString(), item2.getRequest().toString());
    }

    @Test
    void findAllByRequesterId() {
        List<Item> res = itemRepository.findAllByRequesterId(user1.getId());

        assertEquals(res.size(), 1);

        assertEquals(res.get(0).getId(), item2.getId());
        assertEquals(res.get(0).getName(), item2.getName());
        assertEquals(res.get(0).getDescription(), item2.getDescription());
        assertEquals(res.get(0).getAvailable(), item2.getAvailable());
        assertEquals(res.get(0).getOwner().toString(), item2.getOwner().toString());
        assertEquals(res.get(0).getRequest().toString(), item2.getRequest().toString());
        assertEquals(res.get(0).getRequest().getRequester().getId(), user1.getId());
    }
}