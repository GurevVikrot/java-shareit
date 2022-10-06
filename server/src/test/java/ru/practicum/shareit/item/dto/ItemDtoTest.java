package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ItemDtoTest {
    private ItemDto itemDto;

    @Autowired
    private JacksonTester<ItemDto> json;

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
}