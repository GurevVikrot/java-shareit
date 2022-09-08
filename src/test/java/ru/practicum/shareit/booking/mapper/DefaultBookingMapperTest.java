package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class DefaultBookingMapperTest {
    private final User user = new User(1L, "Vitya", "vitya@mail.ru");
    private final UserDto userDto = new UserDto(1L, "Vitya", "vitya@mail.ru");
    private final User booker = new User(2L, "Booker", "booker@mail.ru");
    private final Item item = new Item(1L, "Вещь", "Супер", true, user, null);
    private final ItemDto itemDto = new ItemDto(1L, "Вещь", "Супер", true, null);
    @Mock
    private ItemMapper mokItemMapper;
    @Mock
    private UserMapper mokUserMapper;
    @InjectMocks
    private DefaultBookingMapper bookingMapper;
    private Booking booking;
    private BookingDto bookingDto;
    private RequestBookingDto requestBookingDto;
    private ResponseBookingDto bookingDto1;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(3);

    @BeforeEach
    void beforeEach() {
        booking = new Booking(1L, start, end, item, booker, BookingStatus.APPROVED);
        bookingDto = new BookingDto(1L, start, end, 2L, BookingStatus.APPROVED);
        requestBookingDto = new RequestBookingDto(1L, start, end);
        bookingDto1 = new ResponseBookingDto(1L, start, end, itemDto, userDto, BookingStatus.APPROVED);
    }

    @Test
    void toBookingTest() {
        Booking booking1 = bookingMapper.toBooking(requestBookingDto);

        assertEquals(0L, booking1.getId());
        assertEquals(requestBookingDto.getStart(), booking1.getStart());
        assertEquals(requestBookingDto.getEnd(), booking1.getEnd());
        assertNull(booking1.getBooker());
        assertNull(booking1.getItem());
        assertNull(booking1.getStatus());
    }

    @Test
    void toResponseBookingTest() {
        Mockito
                .when(mokItemMapper.toItemDto(Mockito.any(Item.class)))
                .thenReturn(itemDto);

        Mockito
                .when(mokUserMapper.toUserDto(Mockito.any(User.class)))
                .thenReturn(userDto);

        ResponseBookingDto responseBookingDto1 = bookingMapper.toResponseBooking(booking);

        assertEquals(1L, responseBookingDto1.getId());
        assertEquals(start, responseBookingDto1.getStart());
        assertEquals(end, responseBookingDto1.getEnd());
        assertEquals(itemDto, responseBookingDto1.getItem());
        assertEquals(userDto, responseBookingDto1.getBooker());
        assertEquals(BookingStatus.APPROVED, responseBookingDto1.getStatus());
    }

    @Test
    void setBookingDtoTest() {
        Mockito
                .when(mokUserMapper.toUserDto(Mockito.any(User.class)))
                .thenReturn(userDto);

        BookingDto bookingDto1 = bookingMapper.toItemBooking(booking);
        assertEquals(1L, bookingDto1.getId());
        assertEquals(start, bookingDto1.getStart());
        assertEquals(end, bookingDto1.getEnd());
        assertEquals(userDto.getId(), bookingDto1.getBookerId());
        assertEquals(BookingStatus.APPROVED, bookingDto1.getStatus());
    }
}