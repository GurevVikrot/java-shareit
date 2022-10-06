package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ResponseBookingDtoTest {
    private ResponseBookingDto bookingDto;
    private ItemDto itemDto;
    private UserDto userDto;

    @Autowired
    private JacksonTester<ResponseBookingDto> json;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь", "Супер", true, null);
        userDto = new UserDto(1L, "Vitya", "vitya@mail.ru");

        bookingDto = new ResponseBookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                itemDto,
                userDto,
                BookingStatus.WAITING);
    }

    @Test
    void testUserDto() throws IOException {
        JsonContent<ResponseBookingDto> jsonUser = json.write(bookingDto);

        assertThat(jsonUser).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonUser).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(jsonUser).extractingJsonPathStringValue("$.end").isNotNull();
        assertThat(jsonUser).extractingJsonPathValue("$.item").isNotNull();
        assertThat(jsonUser).extractingJsonPathNumberValue("$.item.id").isEqualTo((int) itemDto.getId());
        assertThat(jsonUser).extractingJsonPathStringValue("$.item.name").isEqualTo(itemDto.getName());
        assertThat(jsonUser).extractingJsonPathStringValue("$.item.description").isEqualTo(itemDto.getDescription());
        assertThat(jsonUser).extractingJsonPathBooleanValue("$.item.available").isEqualTo(itemDto.getAvailable());
        assertThat(jsonUser).extractingJsonPathStringValue("$.item.requestId").isEqualTo(itemDto.getRequestId());
        assertThat(jsonUser).extractingJsonPathValue("$.booker").isNotNull();
        assertThat(jsonUser).extractingJsonPathNumberValue("$.booker.id").isEqualTo((int) userDto.getId());
        assertThat(jsonUser).extractingJsonPathStringValue("$.booker.name").isEqualTo(userDto.getName());
        assertThat(jsonUser).extractingJsonPathStringValue("$.booker.email").isEqualTo(userDto.getEmail());
        assertThat(jsonUser).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());

    }

}