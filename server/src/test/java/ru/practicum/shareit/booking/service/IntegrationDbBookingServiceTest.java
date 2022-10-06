package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.status.State.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationDbBookingServiceTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final DbBookingService bookingService;
    private final User user = new User(1L, "Vitya", "vitya@mail.ru");
    private final User booker = new User(2L, "Booker", "booker@mail.ru");
    private final Item item = new Item(1L, "Вещь", "Супер", true, user, null);
    private LocalDateTime now;
    private Booking bookingPast;
    private Booking bookingCurrent;
    private Booking bookingFuture;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        bookingPast = new Booking(null, now.minusDays(3), now.minusDays(2), item, booker, BookingStatus.CANCELED);
        bookingCurrent = new Booking(null, now.minusDays(1), now.plusDays(3), item, booker, BookingStatus.APPROVED);
        bookingFuture = new Booking(null, now.plusDays(4), now.plusDays(5), item, booker, BookingStatus.WAITING);

        userRepository.save(user);
        userRepository.save(booker);
        itemRepository.save(item);
        bookingRepository.save(bookingPast);
        bookingRepository.save(bookingCurrent);
        bookingRepository.save(bookingFuture);
    }

    @Test
    void getUserBookingsAllTest() {
        List<ResponseBookingDto> bookings = bookingService.getUserBookings(ALL, 2, 0, 10);

        assertEquals(3, bookings.size());
        assertEquals(3, bookings.get(0).getId());
        assertEquals(bookingFuture.getStart().toLocalDate(), bookings.get(0).getStart().toLocalDate());
        assertEquals(bookingFuture.getEnd().toLocalDate(), bookings.get(0).getEnd().toLocalDate());
        assertEquals(2, bookings.get(1).getId());
        assertEquals(bookingCurrent.getStart().toLocalDate(), bookings.get(1).getStart().toLocalDate());
        assertEquals(bookingCurrent.getEnd().toLocalDate(), bookings.get(1).getEnd().toLocalDate());
        assertEquals(1, bookings.get(2).getId());
        assertEquals(bookingPast.getStart().toLocalDate(), bookings.get(2).getStart().toLocalDate());
        assertEquals(bookingPast.getEnd().toLocalDate(), bookings.get(2).getEnd().toLocalDate());
    }

    @Test
    void getUserBookingsPastTest() {
        List<ResponseBookingDto> bookings = bookingService.getUserBookings(PAST, 2, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(bookingPast.getStart().toLocalDate(), bookings.get(0).getStart().toLocalDate());
        assertEquals(bookingPast.getEnd().toLocalDate(), bookings.get(0).getEnd().toLocalDate());
        assertEquals(BookingStatus.CANCELED, bookings.get(0).getStatus());
    }

    @Test
    void getUserBookingsCurrenTest() {
        List<ResponseBookingDto> bookings = bookingService.getUserBookings(CURRENT, 2, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(2, bookings.get(0).getId());
        assertEquals(bookingCurrent.getStart().toLocalDate(), bookings.get(0).getStart().toLocalDate());
        assertEquals(bookingCurrent.getEnd().toLocalDate(), bookings.get(0).getEnd().toLocalDate());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());

    }

    @Test
    void getUserBookingsFutureTest() {
        List<ResponseBookingDto> bookings = bookingService.getUserBookings(FUTURE, 2, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(3, bookings.get(0).getId());
        assertEquals(bookingFuture.getStart().toLocalDate(), bookings.get(0).getStart().toLocalDate());
        assertEquals(bookingFuture.getEnd().toLocalDate(), bookings.get(0).getEnd().toLocalDate());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getUserBookingsWaitingTest() {
        List<ResponseBookingDto> bookings = bookingService.getUserBookings(WAITING, 2, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(3, bookings.get(0).getId());
        assertEquals(bookingFuture.getStart().toLocalDate(), bookings.get(0).getStart().toLocalDate());
        assertEquals(bookingFuture.getEnd().toLocalDate(), bookings.get(0).getEnd().toLocalDate());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void getUserBookingsRejectedTest() {
        bookingFuture.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(bookingFuture);

        List<ResponseBookingDto> bookings = bookingService.getUserBookings(REJECTED, 2, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(3, bookings.get(0).getId());
        assertEquals(bookingFuture.getStart().toLocalDate(), bookings.get(0).getStart().toLocalDate());
        assertEquals(bookingFuture.getEnd().toLocalDate(), bookings.get(0).getEnd().toLocalDate());
        assertEquals(BookingStatus.REJECTED, bookings.get(0).getStatus());
    }
}