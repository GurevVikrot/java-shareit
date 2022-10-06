package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationDbUserServiceTest {
    private final DbUserService userService;
    private final UserRepository userRepository;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(0L, "Vitya", "Vitya@mail.ru");
    }

    @Test
    void correctUpdateUserTest() {
        assertEquals(new UserDto(1L, "Vitya", "Vitya@mail.ru"), userService.createUser(userDto));

        userDto.setId(1L);
        userDto.setName("Up");
        userDto.setEmail("Date@mail.ru");

        assertEquals(userDto, userService.updateUser(userDto, 1L));

        Optional<User> userOptional = userRepository.findById(1L);
        AssertionsForClassTypes.assertThat(userOptional).isPresent();
        User userFromBd = userOptional.orElseThrow();

        assertThat(userFromBd.getId(), notNullValue());
        assertThat(userFromBd.getId(), equalTo(1L));
        assertThat(userFromBd.getName(), notNullValue());
        assertThat(userFromBd.getName(), equalTo("Up"));
        assertThat(userFromBd.getEmail(), notNullValue());
        assertThat(userFromBd.getEmail(), equalTo("Date@mail.ru"));
    }

    @Test
    void updateWithNullFields() {
        userService.createUser(userDto);

        userDto.setName("Up");
        userDto.setEmail(null);

        assertEquals(new UserDto(1L, "Up", "Vitya@mail.ru"),
                userService.updateUser(userDto, 1L));

        Optional<User> userOptional = userRepository.findById(1L);
        AssertionsForClassTypes.assertThat(userOptional).isPresent();
        User userFromBd = userOptional.orElseThrow();

        assertThat(userFromBd.getId(), notNullValue());
        assertThat(userFromBd.getId(), equalTo(1L));
        assertThat(userFromBd.getName(), notNullValue());
        assertThat(userFromBd.getName(), equalTo("Up"));
        assertThat(userFromBd.getEmail(), notNullValue());
        assertThat(userFromBd.getEmail(), equalTo("Vitya@mail.ru"));

        userDto.setName(null);
        userDto.setEmail("Date@mail.ru");

        assertEquals(new UserDto(1L, "Up", "Date@mail.ru"),
                userService.updateUser(userDto, 1L));

        Optional<User> userOptional1 = userRepository.findById(1L);
        AssertionsForClassTypes.assertThat(userOptional1).isPresent();
        User userFromBd1 = userOptional1.orElseThrow();

        assertThat(userFromBd1.getId(), notNullValue());
        assertThat(userFromBd1.getId(), equalTo(1L));
        assertThat(userFromBd1.getName(), notNullValue());
        assertThat(userFromBd1.getName(), equalTo("Up"));
        assertThat(userFromBd1.getEmail(), notNullValue());
        assertThat(userFromBd1.getEmail(), equalTo("Date@mail.ru"));

    }

}