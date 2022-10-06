package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ItemDtoBookingsTest {
    private final BookingDto lastBookingDto = new BookingDto(1,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1), 1L, BookingStatus.APPROVED);
    private final BookingDto nextBookingDto = new BookingDto(2,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(3), 2L, BookingStatus.APPROVED);
    private final CommentDto comment = new CommentDto(1, "Балдеж", "Vova", lastBookingDto.getEnd());
    private ItemDtoBookings itemDto;

    @Autowired
    private JacksonTester<ItemDtoBookings> json;

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

        assertThat(jsonItem).extractingJsonPathNumberValue("$.lastBooking.id").isNotNull();
        assertThat(jsonItem).extractingJsonPathStringValue("$.lastBooking.start").isNotNull();
        assertThat(jsonItem).extractingJsonPathStringValue("$.lastBooking.end").isNotNull();
        assertThat(jsonItem).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo((int) itemDto.getLastBooking().getBookerId());
        assertThat(jsonItem).extractingJsonPathStringValue("$.lastBooking.status")
                .isEqualTo(itemDto.getLastBooking().getStatus().toString());

        assertThat(jsonItem).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo((int) nextBookingDto.getId());
        assertThat(jsonItem).extractingJsonPathStringValue("$.nextBooking.start").isNotNull();
        assertThat(jsonItem).extractingJsonPathStringValue("$.nextBooking.end").isNotNull();
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
        assertThat(jsonItem).extractingJsonPathStringValue("$.comments.[0].created").isNotNull();
    }
}