package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingMapper {
    Booking toBooking(RequestBookingDto requestBookingDto);

    ResponseBookingDto toResponseBooking(Booking booking);

    BookingDto toItemBooking(Booking booking);
}
