package ru.practicum.shareit.item.comment.dto;

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
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class CommentDtoTest {
    private static Validator validator;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
    private CommentDto commentDto;
    @Autowired
    private JacksonTester<CommentDto> json;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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
        assertThat(jsonUser).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().format(formatter));
    }

    @Test
    void validatonTest() {
        commentDto.setText("");
        assertEquals(1, validator.validate(commentDto).size());

        commentDto.setText(" ");
        assertEquals(1, validator.validate(commentDto).size());

        commentDto.setText(null);
        assertEquals(1, validator.validate(commentDto).size());

        commentDto.setText("a".repeat(522));
        assertEquals(1, validator.validate(commentDto).size());

        commentDto.setText("text");
        assertEquals(0, validator.validate(commentDto).size());
    }
}