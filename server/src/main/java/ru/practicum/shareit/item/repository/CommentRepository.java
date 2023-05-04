package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem_Id(Long itemId);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.item.id IN :itemsId " +
            "ORDER BY c.id ASC ")
    List<Comment> findAllByItemsId(@Param("itemsId") List<Long> itemsId);

}