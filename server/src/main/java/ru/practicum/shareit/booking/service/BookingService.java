package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.status.State;

import java.util.List;

public interface BookingService {
    ResponseBookingDto create(RequestBookingDto booking, long userId);

    ResponseBookingDto approveBooking(long bookingId, long userId, boolean approved);

    ResponseBookingDto getBooking(long bookingId, long userId);

    List<ResponseBookingDto> getUserBookings(State state, long userId, int from, int size);

    List<ResponseBookingDto> getOwnerBookings(State state, long userId, int from, int size);
}
