package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DbBookingService implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public DbBookingService(UserRepository userRepository,
                            ItemRepository itemRepository,
                            BookingRepository bookingRepository,
                            BookingMapper bookingMapper) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public ResponseBookingDto create(RequestBookingDto bookingDto, long bookerId) {
        if (!userRepository.existsById(bookerId)) {
            throw new StorageException("Пользователя не существует");
        }
        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new StorageException("Вещи не существует");
        }

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new StorageException("Ошибка получения вещи"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        } else if (item.getOwner().getId() == bookerId) {
            throw new StorageException("Вещь не доступна для бронирования");
        }

        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(userRepository.findById(bookerId).orElseThrow(
                () -> new StorageException("Ошибка получения пользователя")));
        booking.setStatus(BookingStatus.WAITING);

        return bookingMapper.toResponseBooking(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto approveBooking(long bookingId, long userId, boolean approved) {
        if (!userRepository.existsById(userId)) {
            throw new StorageException("Пользователь не найден");
        }
        if (!bookingRepository.existsById(bookingId)) {
            throw new StorageException("Бронирования не существует");
        }

        Booking booking = getBookingFromOptional(bookingRepository.findById(bookingId));

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Изменение статуса не возможно");
        }

        if (booking.getItem().getOwner().getId() != userId) {
            throw new StorageException("Подтвердить бронирование может только владелец вещи");
        } else if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingMapper.toResponseBooking(bookingRepository.save(booking));
    }

    @Override
    public ResponseBookingDto getBooking(long bookingId, long userId) {
        if (!bookingRepository.existsById(bookingId) || !userRepository.existsById(userId)) {
            throw new StorageException("Бронирования не существует");
        }

        Booking booking = getBookingFromOptional(bookingRepository.findById(bookingId));

        if (userId == booking.getBooker().getId() ||
                userId == booking.getItem().getOwner().getId()) {
            return bookingMapper.toResponseBooking(booking);

        }

        throw new StorageException("Нет доступа к бронированию");
    }

    @Override
    public List<ResponseBookingDto> getUserBookings(State state, long userId) {

        if (!userRepository.existsById(userId)) {
            throw new StorageException("Пользователь не найден");
        }

        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_Id(userId, getSort());
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(
                        userId, LocalDateTime.now(), getSort());
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), getSort());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_idAndStartIsAfter(userId, LocalDateTime.now(), getSort());
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusIs(userId, BookingStatus.WAITING, getSort());
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusIs(userId, BookingStatus.REJECTED, getSort());
                break;
        }

        return bookings.stream()
                .map(bookingMapper::toResponseBooking)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseBookingDto> getOwnerBookings(State state, long userId) {

        if (!userRepository.existsById(userId)) {
            throw new StorageException("Пользователь не найден");
        }

        if (itemRepository.findAllByOwner_IdOrderById(userId).isEmpty()) {
            return new ArrayList<>();
        }

        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findOwnerBookings(userId);
                break;
            case PAST:
                bookings = bookingRepository.findPastOwnerBookings(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentOwnerBookings(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureOwnerBookings(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findOwnerBookings(userId).stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookingRepository.findOwnerBookings(userId).stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
                break;
        }

        return bookings.stream()
                .map(bookingMapper::toResponseBooking)
                .collect(Collectors.toList());
    }

    private Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "start");
    }

    private Booking getBookingFromOptional(Optional<Booking> bookingOptional) {
        return bookingOptional.orElseThrow(() -> new StorageException("Ошибка получения бронирования"));
    }
}
