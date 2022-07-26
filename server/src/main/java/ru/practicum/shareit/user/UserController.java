package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Для создания получен пользователь: {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @PathVariable long id) {
        log.info("Обновление id = {} пользователя: {}", id, userDto);
        return userService.updateUser(userDto, id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("Получен запрос на получение пользователя {}", id);
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable long id) {
        log.info("Получен запрос на удаление пользователя {}", id);
        return userService.deleteUser(id);
    }
}
