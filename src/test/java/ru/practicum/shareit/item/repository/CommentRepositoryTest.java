package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    Comment comment10;
    Comment comment11;
    Comment comment20;
    Comment comment21;
    User user1;
    User user2;
    Item item1;
    Item item2;

    @BeforeAll
    public void beforeAll() {
        user1 = new User(1L, "user1", "mail1@ya.ru");
        user2 = new User(2L, "user2", "mail2@ya.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        item1 = new Item(1L, "item1", "desc1", true, user1, null);
        item2 = new Item(2L, "item2", "desc2", true, user2, null);
        itemRepository.save(item1);
        itemRepository.save(item2);
        comment10 = new Comment(1L, "text10", item1, user2, LocalDateTime.now());
        comment11 = new Comment(2L, "text11", item1, user2, LocalDateTime.now());
        comment20 = new Comment(3L, "text20", item2, user1, LocalDateTime.now());
        comment21 = new Comment(4L, "text21", item2, user1, LocalDateTime.now());
        commentRepository.save(comment10);
        commentRepository.save(comment11);
        commentRepository.save(comment20);
        commentRepository.save(comment21);
    }

    @Test
    void findAllByItemId() {
        List<Comment> res = commentRepository.findAllByItem_Id(item1.getId());

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getId(), comment10.getId());
        assertEquals(res.get(0).getText(), comment10.getText());
        assertEquals(res.get(0).getItem().getId(), comment10.getItem().getId());
        assertEquals(res.get(0).getItem().getName(), comment10.getItem().getName());
        assertEquals(res.get(0).getItem().getDescription(), comment10.getItem().getDescription());
        assertEquals(res.get(0).getItem().getAvailable(), comment10.getItem().getAvailable());
        assertEquals(res.get(0).getItem().getOwner().getId(), comment10.getItem().getOwner().getId());
        assertEquals(res.get(0).getItem().getOwner().getName(), comment10.getItem().getOwner().getName());
        assertEquals(res.get(0).getItem().getOwner().getEmail(), comment10.getItem().getOwner().getEmail());
        assertEquals(res.get(0).getItem().getRequest(), comment10.getItem().getRequest());
        assertEquals(res.get(0).getAuthor().getId(), comment10.getAuthor().getId());
        assertEquals(res.get(0).getAuthor().getName(), comment10.getAuthor().getName());
        assertEquals(res.get(0).getAuthor().getEmail(), comment10.getAuthor().getEmail());

        assertEquals(res.get(1).getId(), comment11.getId());
        assertEquals(res.get(1).getText(), comment11.getText());
        assertEquals(res.get(1).getItem().getId(), comment11.getItem().getId());
        assertEquals(res.get(1).getItem().getName(), comment11.getItem().getName());
        assertEquals(res.get(1).getItem().getDescription(), comment11.getItem().getDescription());
        assertEquals(res.get(1).getItem().getAvailable(), comment11.getItem().getAvailable());
        assertEquals(res.get(1).getItem().getOwner().getId(), comment11.getItem().getOwner().getId());
        assertEquals(res.get(1).getItem().getOwner().getName(), comment11.getItem().getOwner().getName());
        assertEquals(res.get(1).getItem().getOwner().getEmail(), comment11.getItem().getOwner().getEmail());
        assertEquals(res.get(1).getItem().getRequest(), comment11.getItem().getRequest());
        assertEquals(res.get(1).getAuthor().getId(), comment11.getAuthor().getId());
        assertEquals(res.get(1).getAuthor().getName(), comment11.getAuthor().getName());
        assertEquals(res.get(1).getAuthor().getEmail(), comment11.getAuthor().getEmail());
    }

    @Test
    void findAllByItemsId() {
        List<Long> itemsId = List.of(2L);

        List<Comment> res = commentRepository.findAllByItemsId(itemsId);

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getId(), comment20.getId());
        assertEquals(res.get(0).getText(), comment20.getText());
        assertEquals(res.get(0).getItem().getId(), comment20.getItem().getId());
        assertEquals(res.get(0).getItem().getName(), comment20.getItem().getName());
        assertEquals(res.get(0).getItem().getDescription(), comment20.getItem().getDescription());
        assertEquals(res.get(0).getItem().getAvailable(), comment20.getItem().getAvailable());
        assertEquals(res.get(0).getItem().getOwner().getId(), comment20.getItem().getOwner().getId());
        assertEquals(res.get(0).getItem().getOwner().getName(), comment20.getItem().getOwner().getName());
        assertEquals(res.get(0).getItem().getOwner().getEmail(), comment20.getItem().getOwner().getEmail());
        assertEquals(res.get(0).getItem().getRequest(), comment20.getItem().getRequest());
        assertEquals(res.get(0).getAuthor().getId(), comment20.getAuthor().getId());
        assertEquals(res.get(0).getAuthor().getName(), comment20.getAuthor().getName());
        assertEquals(res.get(0).getAuthor().getEmail(), comment20.getAuthor().getEmail());

        assertEquals(res.get(1).getId(), comment21.getId());
        assertEquals(res.get(1).getText(), comment21.getText());
        assertEquals(res.get(1).getItem().getId(), comment21.getItem().getId());
        assertEquals(res.get(1).getItem().getName(), comment21.getItem().getName());
        assertEquals(res.get(1).getItem().getDescription(), comment21.getItem().getDescription());
        assertEquals(res.get(1).getItem().getAvailable(), comment21.getItem().getAvailable());
        assertEquals(res.get(1).getItem().getOwner().getId(), comment21.getItem().getOwner().getId());
        assertEquals(res.get(1).getItem().getOwner().getName(), comment21.getItem().getOwner().getName());
        assertEquals(res.get(1).getItem().getOwner().getEmail(), comment21.getItem().getOwner().getEmail());
        assertEquals(res.get(1).getItem().getRequest(), comment21.getItem().getRequest());
        assertEquals(res.get(1).getAuthor().getId(), comment21.getAuthor().getId());
        assertEquals(res.get(1).getAuthor().getName(), comment21.getAuthor().getName());
        assertEquals(res.get(1).getAuthor().getEmail(), comment21.getAuthor().getEmail());
    }
}