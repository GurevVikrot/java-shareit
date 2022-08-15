package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Все бронирования пользователя
    List<Booking> findByBooker_Id(long bookerId, Sort sort);

    // Прошлые бронирования
    List<Booking> findByBooker_IdAndEndIsBefore(long bookerId, LocalDateTime endDate, Sort sort);

    // Текущие бронирования
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(
            long bookerId, LocalDateTime nowStart, LocalDateTime nowEnd, Sort sort);

    // Будущие бронирования пользователя
    List<Booking> findByBooker_idAndStartIsAfter(
            long bookerId, LocalDateTime now, Sort sort);

    // Заявки со статусом - status = WAITING, REJECTED
    List<Booking> findByBooker_IdAndStatusIs(long bookerId, BookingStatus status, Sort sort);

    @Query(value = "SELECT * " +
            "FROM bookings " +
            "WHERE booker_id = ?1 " +
            "AND item_id = ?2 " +
            "ORDER BY end_date ASC " +
            "LIMIT 1;", nativeQuery = true)
    Optional<Booking> findLastBooker_IdAndItem_Id(long bookerId, long itemId);

    @Query(value = "SELECT * " +
            "FROM bookings as b " +
            "INNER JOIN items as i ON b.item_id = i.item_id " +
            "WHERE i.owner = ?1 " +
            "ORDER BY b.start_date DESC;", nativeQuery = true)
    List<Booking> findOwnerBookings(long userId);

    @Query(value = "SELECT * " +
            "FROM bookings as b " +
            "INNER JOIN items as i ON b.item_id = i.item_id " +
            "WHERE i.owner = ?1 " +
            "AND b.end_date < ?2 " +
            "ORDER BY b.start_date DESC;", nativeQuery = true)
    List<Booking> findPastOwnerBookings(long userId, LocalDateTime now);

    @Query(value = "SELECT * " +
            "FROM bookings as b " +
            "INNER JOIN items as i ON b.item_id = i.item_id " +
            "WHERE i.owner = ?1 " +
            "AND ?2 BETWEEN b.start_date AND b.end_date " +
            "ORDER BY b.start_date DESC;", nativeQuery = true)
    List<Booking> findCurrentOwnerBookings(long userId, LocalDateTime now);

    @Query(value = "SELECT * " +
            "FROM bookings as b " +
            "INNER JOIN items as i ON b.item_id = i.item_id " +
            "WHERE i.owner = ?1 " +
            "AND b.start_date > ?2 " +
            "ORDER BY b.start_date DESC;", nativeQuery = true)
    List<Booking> findFutureOwnerBookings(long userId, LocalDateTime now);

    @Query(value = "SELECT * " +
            "FROM bookings as b " +
            "WHERE item_id = ?1 " +
            "ORDER BY end_date ASC " +
            "LIMIT 1;", nativeQuery = true)
    Booking getLastBookingForItem(long itemId);

    @Query(value = "SELECT * " +
            "FROM bookings as b " +
            "WHERE item_id = ?1 " +
            "ORDER BY start_date DESC " +
            "LIMIT 1;", nativeQuery = true)
    Booking getNextBookingForItem(long itemId);
}
