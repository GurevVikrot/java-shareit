package ru.practicum.shareit.user.dto;

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
class UserDtoTest {

    private UserDto userDto;

    @Autowired
    private JacksonTester<UserDto> json;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(1L, "Vitya", "vitya@mail.ru");
    }

    @Test
    void testUserDto() throws IOException {
        JsonContent<UserDto> jsonUser = json.write(userDto);

        assertThat(jsonUser).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonUser).extractingJsonPathStringValue("$.name").isEqualTo("Vitya");
        assertThat(jsonUser).extractingJsonPathStringValue("$.email").isEqualTo("vitya@mail.ru");
    }
}