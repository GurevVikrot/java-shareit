package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody RequestBookingDto booking,
                                                @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("получен запрос от user id = {} на бронирование {}", userId, booking);
        return bookingClient.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable @Positive long bookingId,
                                                 @RequestParam @NotNull Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен запрос на подтверждение бронирования {} от пользователя {}", bookingId, userId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable @Positive long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Запрос на получение бронирования {}, bookerId = {}", bookingId, userId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getBookingsForUser(
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size,
            @RequestParam(required = false, defaultValue = "ALL") @NotNull BookingState state,
            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Запрос на получение бронирований типа {} пользователя {}", state, userId);
        return bookingClient.getUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Запрос на получение бронирований типа {} владельца вещей {}", state, userId);
        return bookingClient.getOwnerBookings(state, userId, from, size);
    }
}
