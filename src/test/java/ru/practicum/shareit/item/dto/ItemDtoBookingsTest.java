package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ItemDtoBookingsTest {
    private static Validator validator;
    private final BookingDto lastBookingDto = new BookingDto(1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1), 1L, BookingStatus.APPROVED);
    private final BookingDto nextBookingDto = new BookingDto(2,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(3), 2L, BookingStatus.APPROVED);
    private final CommentDto comment = new CommentDto(1, "Балдеж", "Vova", lastBookingDto.getEnd());
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
    private ItemDtoBookings itemDto;

    @Autowired
    private JacksonTester<ItemDtoBookings> json;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDtoBookings(1L,
                "Вещь",
                "Супер",
                true,
                null,
                lastBookingDto,
                nextBookingDto,
                List.of(comment));
    }

    @Test
    void jsonTestItemBookingDto() throws IOException {
        JsonContent<ItemDtoBookings> jsonItem = json.write(itemDto);

        assertThat(jsonItem).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonItem).extractingJsonPathStringValue("$.name").isEqualTo("Вещь");
        assertThat(jsonItem).extractingJsonPathStringValue("$.description").isEqualTo("Супер");
        assertThat(jsonItem).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonItem).extractingJsonPathNumberValue("$.requestId").isNull();
        assertThat(jsonItem).extractingJsonPathMapValue("$.lastBooking").isNotEmpty();

        assertThat(jsonItem).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo((int) lastBookingDto.getId());
        assertThat(jsonItem).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(itemDto.getLastBooking().getStart().format(formatter));
        assertThat(jsonItem).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(itemDto.getLastBooking().getEnd().format(formatter));
        assertThat(jsonItem).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo((int) itemDto.getLastBooking().getBookerId());
        assertThat(jsonItem).extractingJsonPathStringValue("$.lastBooking.status")
                .isEqualTo(itemDto.getLastBooking().getStatus().toString());

        assertThat(jsonItem).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo((int) nextBookingDto.getId());
        assertThat(jsonItem).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(itemDto.getNextBooking().getStart().format(formatter));
        assertThat(jsonItem).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(itemDto.getNextBooking().getEnd().format(formatter));
        assertThat(jsonItem).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo((int) itemDto.getNextBooking().getBookerId());
        assertThat(jsonItem).extractingJsonPathStringValue("$.nextBooking.status")
                .isEqualTo(itemDto.getNextBooking().getStatus().toString());

        assertThat(jsonItem).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(jsonItem).extractingJsonPathNumberValue("$.comments.[0].id")
                .isEqualTo((int) comment.getId());
        assertThat(jsonItem).extractingJsonPathStringValue("$.comments.[0].text")
                .isEqualTo(comment.getText());
        assertThat(jsonItem).extractingJsonPathStringValue("$.comments.[0].authorName")
                .isEqualTo(comment.getAuthorName());
        assertThat(jsonItem).extractingJsonPathStringValue("$.comments.[0].created")
                .isEqualTo(comment.getCreated().format(formatter));
    }

    @Test
    void validationNameTest() {
        itemDto.setName(null);
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setName("");
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setName(" ");
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setName("a".repeat(51));
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setName("Вещь");
        assertEquals(0, validator.validate(itemDto).size());
    }

    @Test
    void validationDescriptionTest() {
        itemDto.setDescription(null);
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setDescription("");
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setDescription(" ");
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setDescription("a".repeat(301));
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setDescription("Супер");
        assertEquals(0, validator.validate(itemDto).size());
    }

    @Test
    void validationAvailableTest() {
        itemDto.setAvailable(null);
        assertEquals(1, validator.validate(itemDto).size());

        itemDto.setAvailable(true);
        assertEquals(0, validator.validate(itemDto).size());

        itemDto.setAvailable(false);
        assertEquals(0, validator.validate(itemDto).size());
    }
}