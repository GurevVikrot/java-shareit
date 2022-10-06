package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DbUserServiceTest {
    private static DbUserService userService;
    private static UserMapper mokUserMapper;
    private static UserRepository mokUserRepository;
    private UserDto userDto;


    @BeforeEach
    void beforeEach() {
        mokUserMapper = Mockito.mock(UserMapper.class);
        mokUserRepository = Mockito.mock(UserRepository.class);
        userService = new DbUserService(mokUserMapper, mokUserRepository);
        userDto = new UserDto(0L, "Petya", "Petya@mail.ru");
    }

    @Test
    void saveUserTest() {
        Mockito
                .when(mokUserMapper.toUser(Mockito.any(UserDto.class)))
                .thenReturn(new User(0L, "Petya", "Petya@mail.ru"));

        Mockito
                .when(mokUserMapper.toUserDto(Mockito.any(User.class)))
                .thenReturn(new UserDto(1L, "Petya", "Petya@mail.ru"));

        Mockito
                .when(mokUserRepository.existsById(0L))
                .thenReturn(false);

        Mockito
                .when(mokUserRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "Petya", "Petya@mail.ru"));

        assertEquals(new UserDto(1L, "Petya", "Petya@mail.ru"), userService.createUser(userDto));
    }

    @Test
    void saveUserWhenExistIdOrNull() {
        assertThrows(ValidationException.class, () -> userService.createUser(null));

        userDto.setId(1);
        assertThrows(ValidationException.class, () -> userService.createUser(userDto));

        Mockito
                .when(mokUserRepository.existsById(0L))
                .thenReturn(true);

        userDto.setId(0);
        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
    }

    @Test
    void updateUserTest() {
        userDto.setName("Up");
        userDto.setEmail("Date@mail.ru");

        Mockito
                .when(mokUserRepository.existsById(1L))
                .thenReturn(true);

        Mockito
                .when(mokUserMapper.toUser(userDto))
                .thenReturn(new User(1L, "Up", "Date@mail.ru"));

        Mockito
                .when(mokUserRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "Petya", "Petya@mail.ru")));

        Mockito
                .when(mokUserRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "Up", "Date@mail.ru"));

        Mockito
                .when(mokUserMapper.toUserDto(Mockito.any(User.class)))
                .thenReturn(new UserDto(1L, "Up", "Date@mail.ru"));

        assertEquals(userDto, userService.updateUser(userDto, 1));
    }

    @Test
    void updateWhenNotExistId() {
        Mockito
                .when(mokUserRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(ValidationException.class, () -> userService.updateUser(userDto, 1L));
    }

    @Test
    void updateWithNullNameAndEmail() {
        userDto.setName(null);
        userDto.setEmail(null);

        Mockito
                .when(mokUserRepository.existsById(1L))
                .thenReturn(true);

        Mockito
                .when(mokUserMapper.toUser(userDto))
                .thenReturn(new User(0L, null, null));

        Mockito
                .when(mokUserRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "Petya", "Petya@mail.ru")));

        Mockito
                .when(mokUserRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1L, "Petya", "Petya@mail.ru"));

        Mockito
                .when(mokUserMapper.toUserDto(Mockito.any(User.class)))
                .thenReturn(new UserDto(1L, "Petya", "Petya@mail.ru"));

        userDto.setId(1L);
        userDto.setName("Petya");
        userDto.setEmail("Petya@mail.ru");
        assertEquals(userDto, userService.updateUser(userDto, 1));
    }

    @Test
    void getAllUsers() {
        User user1 = new User(1L, "Vitya", "vitya@mail.ru");
        User user2 = new User(2L, "Vova", "vova@mail.ru");
        User user3 = new User(3L, "Petya", "petya@mail.ru");

        Mockito
                .when(mokUserRepository.findAll())
                .thenReturn(List.of(user1, user2, user3));
        Mockito
                .when(mokUserMapper.toUserDto(user1))
                .thenReturn(new UserDto(user1.getId(), user1.getName(), user1.getEmail()));
        Mockito
                .when(mokUserMapper.toUserDto(user2))
                .thenReturn(new UserDto(user2.getId(), user2.getName(), user2.getEmail()));
        Mockito
                .when(mokUserMapper.toUserDto(user3))
                .thenReturn(new UserDto(user3.getId(), user3.getName(), user3.getEmail()));

        assertEquals(List.of(
                        new UserDto(1L, "Vitya", "vitya@mail.ru"),
                        new UserDto(2L, "Vova", "vova@mail.ru"),
                        new UserDto(3L, "Petya", "petya@mail.ru")),
                userService.getAllUsers());

        Mockito.verify(mokUserMapper, Mockito.times(3))
                .toUserDto(Mockito.any(User.class));
    }

    @Test
    void getUserTest() {
        Mockito
                .when(mokUserRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "Petya", "Petya@mail.ru")));

        Mockito
                .when(mokUserRepository.findById(2L))
                .thenReturn(Optional.empty());

        Mockito
                .when(mokUserMapper.toUserDto(Mockito.any(User.class)))
                .thenReturn(new UserDto(1L, "Petya", "Petya@mail.ru"));

        userDto.setId(1L);
        assertEquals(userDto, userService.getUser(1L));
        assertThrows(StorageException.class, () -> userService.getUser(2L));
    }

    @Test
    void deleteUserTest() {
        assertTrue(userService.deleteUser(1L));

        Mockito
                .verify(mokUserRepository, Mockito.times(1))
                .deleteById(1L);
    }
}