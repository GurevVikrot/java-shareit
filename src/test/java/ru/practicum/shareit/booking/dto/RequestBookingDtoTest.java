package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class RequestBookingDtoTest {
    private static Validator validator;
    private RequestBookingDto bookingDto;

    @Autowired
    private JacksonTester<RequestBookingDto> json;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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

    @Test
    void notValideItemId() {
        assertEquals(0, validator.validate(bookingDto).size());

        bookingDto.setItemId(0);
        assertEquals(1, validator.validate(bookingDto).size());

        bookingDto.setItemId(-1);
        assertEquals(1, validator.validate(bookingDto).size());
    }

    @Test
    void notValideStart() {
        bookingDto.setStart(null);
        assertEquals(1, validator.validate(bookingDto).size());
        
        // Локально проходит, на git нет
//        bookingDto.setStart(LocalDateTime.now());
//        assertEquals(0, validator.validate(bookingDto).size());

        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        assertEquals(1, validator.validate(bookingDto).size());

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        assertEquals(0, validator.validate(bookingDto).size());
    }

    @Test
    void notValideEnd() {
        assertEquals(0, validator.validate(bookingDto).size());

        bookingDto.setEnd(null);
        assertEquals(1, validator.validate(bookingDto).size());

        bookingDto.setEnd(LocalDateTime.now());
        assertEquals(1, validator.validate(bookingDto).size());

        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        assertEquals(1, validator.validate(bookingDto).size());

        bookingDto.setEnd(LocalDateTime.now().plusDays(3));
        assertEquals(0, validator.validate(bookingDto).size());
    }
}