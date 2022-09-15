package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ItemRequestDtoTest {
    private static Validator validator;
    private ItemRequestDto itemRequestDto;
    private UserDto userDto;
    private ItemDto itemDto;
    private LocalDateTime now;

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь", "Супер", true, 1L);
        userDto = new UserDto(0L, "Vitya", "vitya@mail.ru");
        now = LocalDateTime.now();
        itemRequestDto = new ItemRequestDto(1L, "Хочется", userDto, List.of(itemDto), now);
    }

    @Test
    void itemRequestDtoJsonConvertTest() throws IOException {
        JsonContent<ItemRequestDto> jsonItemRequest = json.write(itemRequestDto);

        assertThat(jsonItemRequest).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonItemRequest).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(jsonItemRequest).extractingJsonPathValue("$.requester").isNotNull();
        assertThat(jsonItemRequest).extractingJsonPathNumberValue("$.requester.id").isEqualTo((int) userDto.getId());
        assertThat(jsonItemRequest).extractingJsonPathStringValue("$.requester.name").isEqualTo(userDto.getName());
        assertThat(jsonItemRequest).extractingJsonPathStringValue("$.requester.email").isEqualTo(userDto.getEmail());
        assertThat(jsonItemRequest).extractingJsonPathValue("$.items").isNotNull();
        assertThat(jsonItemRequest).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo((int) itemDto.getId());
        assertThat(jsonItemRequest).extractingJsonPathStringValue("$.items.[0].name").isEqualTo(itemDto.getName());
        assertThat(jsonItemRequest).extractingJsonPathStringValue("$.items.[0].description")
                .isEqualTo(itemDto.getDescription());
        assertThat(jsonItemRequest).extractingJsonPathBooleanValue("$.items.[0].available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(jsonItemRequest).extractingJsonPathNumberValue("$.items.[0].requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
        assertThat(jsonItemRequest).extractingJsonPathStringValue("$.created").isNotNull();
    }

    @Test
    void itemRequestDtoValidationTest() {
        itemRequestDto.setDescription("");
        assertEquals(1, validator.validate(itemRequestDto).size());

        itemRequestDto.setDescription(" ");
        assertEquals(1, validator.validate(itemRequestDto).size());

        itemRequestDto.setDescription(null);
        assertEquals(1, validator.validate(itemRequestDto).size());

        itemRequestDto.setDescription("a");
        assertEquals(0, validator.validate(itemRequestDto).size());
    }

    @Test
    void itemRequestWithDifferentValuesValidation() {
        itemRequestDto.setId(-1L);
        assertEquals(0, validator.validate(itemRequestDto).size());

        itemRequestDto.setId(0L);
        itemRequestDto.setRequester(null);
        itemRequestDto.setItems(null);
        itemRequestDto.setCreated(null);
        assertEquals(0, validator.validate(itemRequestDto).size());

        itemRequestDto.setCreated(now.minusMonths(2));
        assertEquals(0, validator.validate(itemRequestDto).size());

        itemRequestDto.setCreated(now.plusMonths(2));
        assertEquals(0, validator.validate(itemRequestDto).size());
    }
}