package ru.practicum.shareit.user.dto;

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
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class UserDtoTest {
    private static Validator validator;
    private UserDto userDto;

    @Autowired
    private JacksonTester<UserDto> json;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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

    @Test
    void validationNameTest() {
        userDto.setName(null);
        assertEquals(1, validator.validate(userDto).size());

        userDto.setName(" ");
        assertEquals(1, validator.validate(userDto).size());

        userDto.setName("");
        assertEquals(1, validator.validate(userDto).size());

        userDto.setName("Vitya");
        assertEquals(0, validator.validate(userDto).size());
    }

    @Test
    void validationEmailTest() {
        userDto.setEmail(null);
        assertEquals(1, validator.validate(userDto).size());

        userDto.setEmail("");
        assertEquals(1, validator.validate(userDto).size());

        userDto.setEmail(" ");
        assertEquals(2, validator.validate(userDto).size());

        userDto.setEmail("email");
        assertEquals(1, validator.validate(userDto).size());

        userDto.setEmail("@mail.ru");
        assertEquals(1, validator.validate(userDto).size());

        userDto.setEmail("@mail");
        assertEquals(1, validator.validate(userDto).size());

        userDto.setEmail("@m.");
        assertEquals(1, validator.validate(userDto).size());

        userDto.setEmail("vitya@mail.ru");
        assertEquals(0, validator.validate(userDto).size());
    }
}