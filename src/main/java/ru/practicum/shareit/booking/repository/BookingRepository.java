package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(Long userId, Status status);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfter(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            Long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long userId, Status status);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1")
    List<Booking> findAllByItemId(Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.item.owner.id = ?2 " +
            "AND b.start <= ?3 " +
            "AND (b.status != 'REJECTED' AND b.status != 'CANCELED')" +
            "ORDER BY b.end DESC ")
    List<Booking> findLastBooking(Long itemId, Long userId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.item.owner.id = ?2 " +
            "AND b.start >= ?3 " +
            "AND (b.status != 'REJECTED' AND b.status != 'CANCELED')" +
            "ORDER BY b.start ASC")
    List<Booking> findNextBooking(Long itemId, Long userId, LocalDateTime now);

    @Query("SELECT (COUNT(b) > 0) " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 " +
            "AND b.end < ?3")
    boolean isItemWasUsedByUser(Long itemId, Long userId, LocalDateTime now);

}