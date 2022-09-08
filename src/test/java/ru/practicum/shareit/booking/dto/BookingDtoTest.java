package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class BookingDtoTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
    private BookingDto bookingDto;

    @Autowired
    private JacksonTester<BookingDto> json;

    @BeforeEach
    void beforeEach() {
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                1L,
                BookingStatus.APPROVED);
    }

    @Test
    void testUserDto() throws IOException {
        JsonContent<BookingDto> jsonUser = json.write(bookingDto);

        assertThat(jsonUser).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonUser).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().format(formatter));
        assertThat(jsonUser).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().format(formatter));
        assertThat(jsonUser).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(jsonUser).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());
    }
}