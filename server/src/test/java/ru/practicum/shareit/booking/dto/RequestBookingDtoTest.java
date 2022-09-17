package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class RequestBookingDtoTest {
    private RequestBookingDto bookingDto;

    @Autowired
    private JacksonTester<RequestBookingDto> json;

    @BeforeEach
    void beforeEach() {
        bookingDto = new RequestBookingDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3));
    }

    @Test
    void testRequestBookingDto() throws IOException {
        JsonContent<RequestBookingDto> jsonUser = json.write(bookingDto);

        assertThat(jsonUser).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonUser).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(jsonUser).extractingJsonPathStringValue("$.end").isNotNull();
    }
}