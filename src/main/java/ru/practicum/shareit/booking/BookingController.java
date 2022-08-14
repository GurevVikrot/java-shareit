package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * // TODO .
 */

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
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Запрос на получение бронирований типа {} пользователя {}",state, userId);
        return bookingService.getUserBookings(state, userId);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getBookingsForOwner(
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Запрос на получение бронирований типа {} владельца вещей {}",state, userId);
        return bookingService.getOwnerBookings(state, userId);
    }
}
