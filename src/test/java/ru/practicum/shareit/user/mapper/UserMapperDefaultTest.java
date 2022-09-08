package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperDefaultTest {
    private static UserMapper userMapper;
    private User user = new User(0L, "Vitya", "vitya@mail.ru");
    private UserDto userDto;

    @BeforeAll
    public static void beforeAll() {
        userMapper = new UserMapperDefault();
    }

    @BeforeEach
    void BeforeEach() {
        user = new User(0L, "Vitya", "vitya@mail.ru");
        userDto = new UserDto(1L, "Petya", "Petya@mail.ru");
    }

    @Test
    void correctConvertToUserDto() {
        UserDto userDtoConverted = userMapper.toUserDto(user);

        assertEquals(0, userDtoConverted.getId());
        assertEquals("Vitya", userDtoConverted.getName());
        assertEquals("vitya@mail.ru", userDtoConverted.getEmail());
    }

    @Test
    void correctConvertToUser() {
        User userConverted = userMapper.toUser(userDto);

        assertEquals(1, userConverted.getId());
        assertEquals("Petya", userConverted.getName());
        assertEquals("Petya@mail.ru", userConverted.getEmail());
    }

    @Test
    void convertToDtoWithNullFields() {
        userDto.setName(null);
        userDto.setEmail(null);

        User userConverted = userMapper.toUser(userDto);

        assertEquals(1, userConverted.getId());
        assertNull(userConverted.getName());
        assertNull(userConverted.getEmail());
    }

    @Test
    void convertToDtoTrimFields() {
        userDto.setName(" Petya  ");
        userDto.setEmail(" Petya@mail.ru  ");

        User userConverted = userMapper.toUser(userDto);

        assertEquals(1, userConverted.getId());
        assertEquals("Petya", userConverted.getName());
        assertEquals("Petya@mail.ru", userConverted.getEmail());
    }
}