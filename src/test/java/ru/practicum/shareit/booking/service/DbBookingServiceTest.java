package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.DefaultBookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.status.State;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapperDefault;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperDefault;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DbBookingServiceTest {
    private final BookingMapper bookingMapper = new DefaultBookingMapper(
            new UserMapperDefault(),
            new ItemMapperDefault());

    private UserRepository mokUserRepository = Mockito.mock(UserRepository.class);
    private ItemRepository mokItemRepository = Mockito.mock(ItemRepository.class);
    private BookingRepository mokBookingRepository = Mockito.mock(BookingRepository.class);
    private DbBookingService bookingService = new DbBookingService(
            mokUserRepository,
            mokItemRepository,
            mokBookingRepository,
            bookingMapper);

    private RequestBookingDto requestBookingDto;
    private ResponseBookingDto responseBookingDto;
    private Booking bookingFromBd;
    private LocalDateTime start = LocalDateTime.now().plusDays(1);
    private LocalDateTime end = LocalDateTime.now().plusDays(3);
    private User user = new User(1L, "Vitya", "vitya@mail.ru");
    private User booker = new User(2L, "Booker", "booker@mail.ru");
    private UserDto bookerDto = new UserDto(2L, "Booker", "booker@mail.ru");
    private Item item = new Item(1L, "Вещь", "Супер", true, user, null);
    private ItemDto itemDto = new ItemDto(1L, "Вещь", "Супер", true, null);


    @BeforeEach
    void beforeEach() {
        requestBookingDto = new RequestBookingDto(1L, start, end);
        responseBookingDto = new ResponseBookingDto(1L, start, end, itemDto, bookerDto, BookingStatus.WAITING);
        bookingFromBd = new Booking(1L, start, end, item, booker, BookingStatus.WAITING);
    }

    @Test
    void correctCreateBookingTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mokUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booker));

        Mockito
                .when(mokBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookingFromBd);

        assertEquals(responseBookingDto, bookingService.create(requestBookingDto, 2L));

        Mockito.verify(mokItemRepository, Mockito.times(1))
                .findById(1L);
        Mockito.verify(mokUserRepository, Mockito.times(1))
                .findById(2L);
        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingWhenBookerNotExist() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> bookingService.create(requestBookingDto, 2L));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingWhenItemNotExist() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> bookingService.create(requestBookingDto, 2L));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingWhenItemNotExistInDB() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(StorageException.class, () -> bookingService.create(requestBookingDto, 2L));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingWhenBookerNotExistInDB() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(mokUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(StorageException.class, () -> bookingService.create(requestBookingDto, 2L));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingWhenItemNotAvailable() {
        item.setAvailable(false);

        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(requestBookingDto, 2L));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));

        item.setAvailable(true);
    }

    @Test
    void createBookingWhenBookerIsOwner() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(StorageException.class, () -> bookingService.create(requestBookingDto, 1L));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void correctApproveBookingTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingFromBd));

        Mockito
                .when(mokBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookingFromBd);

        responseBookingDto.setStatus(BookingStatus.APPROVED);
        assertEquals(responseBookingDto, bookingService.approveBooking(1L, 1L, true));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findById(1L);

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .save(Mockito.any(Booking.class));
    }

    @Test
    void approveBookingWhenUserNotExist() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> bookingService.approveBooking(1L, 1L, true));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void approveBookingWhenBookingNotExist() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> bookingService.approveBooking(1L, 1L, true));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void aproveBookingWhenBookingNotExistInDB() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(StorageException.class, () -> bookingService.approveBooking(1L, 1L, true));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void approveBookingWhenBookingStatusNotWaiting() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingFromBd));

        bookingFromBd.setStatus(BookingStatus.APPROVED);
        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 1L, true));

        bookingFromBd.setStatus(BookingStatus.REJECTED);
        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 1L, true));

        bookingFromBd.setStatus(BookingStatus.CANCELED);
        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 1L, true));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void approveBookingWhenNotOwner() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingFromBd));

        assertThrows(StorageException.class, () -> bookingService.approveBooking(1L, 3L, true));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .save(Mockito.any(Booking.class));
    }

    @Test
    void rejectBookingTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingFromBd));

        Mockito
                .when(mokBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookingFromBd);

        responseBookingDto.setStatus(BookingStatus.REJECTED);
        assertEquals(responseBookingDto, bookingService.approveBooking(1L, 1L, false));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findById(1L);

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .save(Mockito.any(Booking.class));
    }

    @Test
    void correctGetBookingToBookerTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingFromBd));

        assertEquals(responseBookingDto, bookingService.getBooking(1L, 2L));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findById(1L);
    }

    @Test
    void correctGetBookingToOwnerTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingFromBd));

        assertEquals(responseBookingDto, bookingService.getBooking(1L, 1L));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findById(1L);
    }

    @Test
    void getBookingWhenBookingNotExist() {
        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> bookingService.getBooking(1L, 2L));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .findById(Mockito.anyLong());
    }

    @Test
    void getBookingWhenBookerNotExist() {
        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> bookingService.getBooking(1L, 2L));

        Mockito.verify(mokBookingRepository, Mockito.never())
                .findById(Mockito.anyLong());
    }

    @Test
    void getBookingWhenNotBookerOrOwnerExist() {
        Mockito
                .when(mokBookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        assertThrows(StorageException.class, () -> bookingService.getBooking(1L, 3L));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void getAllUserBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findByBooker_IdIs(2L, PageRequest
                        .of(0, 10, Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getUserBookings(State.ALL, 2L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findByBooker_IdIs(2L, PageRequest.of(0, 10,
                        Sort.by(Sort.Direction.DESC, "start")));
    }

    @Test
    void getPastUserBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findByBooker_IdAndEndIsBefore(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getUserBookings(State.PAST, 2L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findByBooker_IdAndEndIsBefore(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getCurrentUserBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getUserBookings(State.CURRENT, 2L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findByBooker_IdAndStartIsBeforeAndEndIsAfter(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getFutureUserBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findByBooker_idAndStartIsAfter(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getUserBookings(State.FUTURE, 2L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findByBooker_idAndStartIsAfter(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getWaitingUserBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findByBooker_IdAndStatusIs(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getUserBookings(State.WAITING, 2L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findByBooker_IdAndStatusIs(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getRejectedUserBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokBookingRepository.findByBooker_IdAndStatusIs(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getUserBookings(State.REJECTED, 2L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findByBooker_IdAndStatusIs(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getUserBookingsWhenUserNotExsixt() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> bookingService.getUserBookings(State.ALL, 2L, 0, 10));
    }

    @Test
    void getAllOwnerBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        Mockito
                .when(mokBookingRepository.findOwnerBookings(1L, PageRequest.of(0, 10)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getOwnerBookings(State.ALL, 1L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findOwnerBookings(1L, PageRequest.of(0, 10));
    }

    @Test
    void getOwnerBookingsWhenOwnerNotExist() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> bookingService.getOwnerBookings(State.ALL, 1L, 0, 10));
    }

    @Test
    void getOwnerBookingsWhenOwnerNotHaveItems() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        assertTrue(bookingService.getOwnerBookings(State.ALL, 1L, 0, 10).isEmpty());
    }

    @Test
    void getPastOwnerBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        Mockito
                .when(mokBookingRepository.findPastOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getOwnerBookings(State.PAST, 1L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findPastOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getCurrentOwnerBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        Mockito
                .when(mokBookingRepository.findCurrentOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getOwnerBookings(State.CURRENT, 1L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findCurrentOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getFutureOwnerBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        Mockito
                .when(mokBookingRepository.findFutureOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getOwnerBookings(State.FUTURE, 1L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findFutureOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getWaitingOwnerBookingsTest() {
        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        Mockito
                .when(mokBookingRepository.findOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getOwnerBookings(State.WAITING, 1L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(Pageable.class));
    }

    @Test
    void getRejectedOwnerBookingsTest() {
        responseBookingDto.setStatus(BookingStatus.REJECTED);
        bookingFromBd.setStatus(BookingStatus.REJECTED);

        Mockito
                .when(mokUserRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(mokItemRepository.findAllByOwner_IdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        Mockito
                .when(mokBookingRepository.findOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(bookingFromBd, bookingFromBd, bookingFromBd));

        List<ResponseBookingDto> response = List.of(responseBookingDto, responseBookingDto, responseBookingDto);

        assertEquals(response, bookingService.getOwnerBookings(State.REJECTED, 1L, 0, 10));

        Mockito.verify(mokBookingRepository, Mockito.times(1))
                .findOwnerBookings(
                        Mockito.anyLong(),
                        Mockito.any(Pageable.class));

        bookingFromBd.setStatus(BookingStatus.WAITING);
    }
}