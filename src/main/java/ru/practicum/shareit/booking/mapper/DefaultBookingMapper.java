package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
public class DefaultBookingMapper implements BookingMapper{
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public DefaultBookingMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    public Booking toBooking(RequestBookingDto requestBookingDto) {
        return new Booking(0L,
                requestBookingDto.getStart(),
                requestBookingDto.getEnd(),
                null,
                null,
                null);
    }

    @Override
    public ResponseBookingDto toResponseBooking(Booking booking) {
        return ResponseBookingDto.builder().
                id(booking.getId()).
                start(booking.getStart()).
                end(booking.getEnd()).
                item(itemMapper.toItemDto(booking.getItem())).
                booker(userMapper.toUserDto(booking.getBooker())).
                status(booking.getStatus()).
                build();
    }

    @Override
    public BookingDto toItemBooking(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                userMapper.toUserDto(booking.getBooker()).getId(),
                booking.getStatus());
    }
}
