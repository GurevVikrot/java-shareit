package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.State;

import java.util.List;

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
    public ResponseBookingDto createBooking(@RequestBody RequestBookingDto booking,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос от user id = {} на бронирование {}", userId, booking);
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approveBooking(@PathVariable long bookingId,
                                             @RequestParam Boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на подтверждение бронирования {} от пользователя {}", bookingId, userId);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@PathVariable long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение бронирования {}, bookerId = {}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<ResponseBookingDto> getBookingsForUser(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение бронирований типа {} пользователя {}", state, userId);
        return bookingService.getUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getBookingsForOwner(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение бронирований типа {} владельца вещей {}", state, userId);
        return bookingService.getOwnerBookings(state, userId, from, size);
    }
}
