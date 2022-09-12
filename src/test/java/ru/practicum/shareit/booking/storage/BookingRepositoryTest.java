package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");
    private LocalDateTime now;
    private User user;
    private User booker;
    private Item item;
    private Booking bookingFuture;
    private Booking bookingPast;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        user = new User(null, "Vitya", "vitya@mail.ru");
        booker = new User(null, "Booker", "booker@mail.ru");
        item = new Item(null, "Вещь", "Супер", true, user, null);
        bookingFuture = new Booking(null, now.plusDays(1), now.plusDays(3), item, booker, BookingStatus.WAITING);
        bookingPast = new Booking(null, now.minusDays(3), now.minusDays(2), item, booker, BookingStatus.CANCELED);
        em.persist(user);
        em.persist(item);
        em.persist(booker);
    }

    @Test
    void findByBookerIdTest() {
        em.persist(bookingFuture);
        em.persist(bookingPast);

        List<Booking> bookingsFromDb5Id = bookingRepository.findByBooker_IdIs(1, PageRequest.of(0, 10, sort));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findByBooker_IdIs(2, PageRequest.of(0, 10, sort));
        assertEquals(2, bookingsFromDb.size());
        assertEquals(bookingFuture, bookingsFromDb.get(0));
        assertEquals(bookingPast, bookingsFromDb.get(1));
    }

    @Test
    void findByBooker_IdAndEndIsBeforeTest() {
        em.persist(bookingFuture);
        em.persist(bookingPast);

        List<Booking> bookingsFromDb5Id = bookingRepository.findByBooker_IdAndEndIsBefore(
                1,
                now,
                PageRequest.of(0, 10, sort));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findByBooker_IdAndEndIsBefore(
                2,
                now,
                PageRequest.of(0, 10, sort));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingPast, bookingsFromDb.get(0));
    }

    @Test
    void findByBooker_IdAndStartIsBeforeAndEndIsAfterTest() {
        Booking bookingCurrent1 = new Booking(null, now, now.plusDays(1), item, booker, BookingStatus.REJECTED);
        Booking bookingCurrent2 = new Booking(null, now.plusMinutes(10), now.plusDays(1), item, booker, BookingStatus.WAITING);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);
        em.persist(bookingCurrent2);

        List<Booking> bookingsFromDb5Id = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                1,
                now.plusHours(1),
                now.plusHours(1),
                PageRequest.of(0, 10, sort));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                2,
                now.plusHours(1),
                now.plusHours(1),
                PageRequest.of(0, 10, sort));

        assertEquals(2, bookingsFromDb.size());
        assertEquals(bookingCurrent2, bookingsFromDb.get(0));
        assertEquals(bookingCurrent1, bookingsFromDb.get(1));
    }

    @Test
    void findByBooker_idAndStartIsAfterTest() {
        Booking bookingCurrent1 = new Booking(null, now, now.plusDays(1), item, booker, BookingStatus.REJECTED);
        Booking bookingCurrent2 = new Booking(
                null, now.plusMinutes(10), now.plusDays(1), item, booker, BookingStatus.WAITING);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);
        em.persist(bookingCurrent2);

        List<Booking> bookingsFromDb5Id = bookingRepository.findByBooker_idAndStartIsAfter(
                1,
                now.plusHours(1),
                PageRequest.of(0, 10, sort));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findByBooker_idAndStartIsAfter(
                2,
                now.plusHours(1),
                PageRequest.of(0, 10, sort));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingFuture, bookingsFromDb.get(0));
    }

    @Test
    void findByBooker_IdAndStatusIsTest() {
        Booking bookingCurrent1 = new Booking(null, now, now.plusDays(1), item, booker, BookingStatus.REJECTED);
        Booking bookingCurrent2 = new Booking(
                null, now.plusMinutes(10), now.plusDays(1), item, booker, BookingStatus.WAITING);
        bookingFuture.setStatus(BookingStatus.APPROVED);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);
        em.persist(bookingCurrent2);

        List<Booking> bookingsFromDb5Id = bookingRepository.findByBooker_IdAndStatusIs(
                1,
                BookingStatus.CANCELED,
                PageRequest.of(0, 10, sort));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findByBooker_IdAndStatusIs(
                2,
                BookingStatus.CANCELED,
                PageRequest.of(0, 10, sort));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingPast, bookingsFromDb.get(0));

        bookingsFromDb5Id = bookingRepository.findByBooker_IdAndStatusIs(
                1,
                BookingStatus.WAITING,
                PageRequest.of(0, 10, sort));
        assertTrue(bookingsFromDb5Id.isEmpty());

        bookingsFromDb = bookingRepository.findByBooker_IdAndStatusIs(
                2,
                BookingStatus.WAITING,
                PageRequest.of(0, 10, sort));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingCurrent2, bookingsFromDb.get(0));

        bookingsFromDb5Id = bookingRepository.findByBooker_IdAndStatusIs(
                1,
                BookingStatus.REJECTED,
                PageRequest.of(0, 10, sort));
        assertTrue(bookingsFromDb5Id.isEmpty());

        bookingsFromDb = bookingRepository.findByBooker_IdAndStatusIs(
                2,
                BookingStatus.REJECTED,
                PageRequest.of(0, 10, sort));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingCurrent1, bookingsFromDb.get(0));

        bookingsFromDb5Id = bookingRepository.findByBooker_IdAndStatusIs(
                1,
                BookingStatus.APPROVED,
                PageRequest.of(0, 10, sort));
        assertTrue(bookingsFromDb5Id.isEmpty());

        bookingsFromDb = bookingRepository.findByBooker_IdAndStatusIs(
                2,
                BookingStatus.APPROVED,
                PageRequest.of(0, 10, sort));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingFuture, bookingsFromDb.get(0));
    }

    @Test
    void findLastBooker_IdAndItem_IdTest() {
        Booking bookingCurrent1 = new Booking(null, now, now.plusDays(1), item, booker, BookingStatus.REJECTED);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);

        Optional<Booking> optionalBookingFromDb = bookingRepository.findLastBooker_IdAndItem_Id(2, 1);
        assertThat(optionalBookingFromDb).isPresent();

        Booking bookingFromDb = optionalBookingFromDb.orElseThrow();
        assertEquals(bookingPast, bookingFromDb);
    }

    @Test
    void findOwnerBookingsTest() {
        Booking bookingCurrent1 = new Booking(null, now, now.plusDays(1), item, booker, BookingStatus.REJECTED);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);

        List<Booking> bookingsFromDb5Id = bookingRepository.findOwnerBookings(
                2,
                PageRequest.of(0, 10));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findOwnerBookings(
                1,
                PageRequest.of(0, 10));

        assertEquals(3, bookingsFromDb.size());
        assertEquals(bookingFuture, bookingsFromDb.get(0));
        assertEquals(bookingCurrent1, bookingsFromDb.get(1));
        assertEquals(bookingPast, bookingsFromDb.get(2));
    }

    @Test
    void findPastOwnerBookingsTest() {
        Booking bookingCurrent1 = new Booking(null, now, now.plusDays(1), item, booker, BookingStatus.REJECTED);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);

        List<Booking> bookingsFromDb5Id = bookingRepository.findPastOwnerBookings(
                2,
                now,
                PageRequest.of(0, 10));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findPastOwnerBookings(
                1,
                now,
                PageRequest.of(0, 10));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingPast, bookingsFromDb.get(0));
    }

    @Test
    void findCurrentOwnerBookingsTest() {
        Booking bookingCurrent1 = new Booking(
                null, now.minusMinutes(10), now.plusDays(1), item, booker, BookingStatus.REJECTED);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);

        List<Booking> bookingsFromDb5Id = bookingRepository.findCurrentOwnerBookings(
                2,
                now,
                PageRequest.of(0, 10));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findCurrentOwnerBookings(
                1,
                now,
                PageRequest.of(0, 10));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingCurrent1, bookingsFromDb.get(0));
    }

    @Test
    void findFutureOwnerBookingsTest() {
        Booking bookingCurrent1 = new Booking(
                null, now.minusMinutes(10), now.plusDays(1), item, booker, BookingStatus.REJECTED);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);

        List<Booking> bookingsFromDb5Id = bookingRepository.findFutureOwnerBookings(
                2,
                now,
                PageRequest.of(0, 10));
        assertTrue(bookingsFromDb5Id.isEmpty());

        List<Booking> bookingsFromDb = bookingRepository.findFutureOwnerBookings(
                1,
                now,
                PageRequest.of(0, 10));

        assertEquals(1, bookingsFromDb.size());
        assertEquals(bookingFuture, bookingsFromDb.get(0));
    }

    @Test
    void getLastBookingForItemTest() {
        Booking bookingCurrent1 = new Booking(
                null, now.minusMinutes(10), now.plusDays(1), item, booker, BookingStatus.REJECTED);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);

        Booking booking = bookingRepository.getLastBookingForItem(1);
        assertEquals(bookingPast, booking);
    }

    @Test
    void getNextBookingForItemTest() {
        Booking bookingCurrent1 = new Booking(
                null, now.minusMinutes(10), now.plusDays(1), item, booker, BookingStatus.REJECTED);

        em.persist(bookingFuture);
        em.persist(bookingPast);
        em.persist(bookingCurrent1);

        Booking booking = bookingRepository.getNextBookingForItem(1);
        assertEquals(bookingFuture, booking);
    }
}