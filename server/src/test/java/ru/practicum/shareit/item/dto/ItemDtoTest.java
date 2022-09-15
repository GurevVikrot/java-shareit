package ru.practicum.shareit.item.dto;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ItemDtoTest {
    private static Validator validator;
    private ItemDto itemDto;

    @Autowired
    private JacksonTester<ItemDto> json;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь", "Супер", true, null);
    }

    @Test
    void testItemDto() throws IOException {
        JsonContent<ItemDto> jsonItem = json.write(itemDto);

        assertThat(jsonItem).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonItem).extractingJsonPathStringValue("$.name").isEqualTo("Вещь");
        assertThat(jsonItem).extractingJsonPathStringValue("$.description").isEqualTo("Супер");
        assertThat(jsonItem).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonItem).extractingJsonPathStringValue("$.requestId").isNull();
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