package ru.practicum.shareit.item.comment.dto;

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
class CommentDtoTest {
    private CommentDto commentDto;
    @Autowired
    private JacksonTester<CommentDto> json;

    @BeforeEach
    void beforeEach() {
        commentDto = new CommentDto(1L, "Балдеж", "Вова", LocalDateTime.now());
    }

    @Test
    void testCommentDto() throws IOException {
        JsonContent<CommentDto> jsonUser = json.write(commentDto);

        assertThat(jsonUser).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonUser).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(jsonUser).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
        assertThat(jsonUser).extractingJsonPathStringValue("$.created").isNotNull();
    }
}