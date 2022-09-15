package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.State;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseBookingDto createBooking(@Valid @RequestBody RequestBookingDto booking,
                                            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("получен запрос от user id = {} на бронирование {}", userId, booking);
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approveBooking(@PathVariable @Positive long bookingId,
                                             @RequestParam @NotNull Boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен запрос на подтверждение бронирования {} от пользователя {}", bookingId, userId);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@PathVariable @Positive long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Запрос на получение бронирования {}, bookerId = {}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<ResponseBookingDto> getBookingsForUser(
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size,
            @RequestParam(required = false, defaultValue = "ALL") @NotNull State state,
            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Запрос на получение бронирований типа {} пользователя {}", state, userId);
        return bookingService.getUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getBookingsForOwner(
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Запрос на получение бронирований типа {} владельца вещей {}", state, userId);
        return bookingService.getOwnerBookings(state, userId, from, size);
    }
}
