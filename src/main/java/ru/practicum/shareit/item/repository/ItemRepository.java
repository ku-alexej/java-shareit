package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<ItemDto> findByOwner_Id(Long userId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true " +
            "AND ((LOWER(i.name) LIKE LOWER(?1)) " +
            "OR (LOWER(i.description) LIKE LOWER (?1)))")
    List<Item> searchAvailableItems(String text);
}
