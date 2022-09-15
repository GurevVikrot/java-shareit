package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Для создания получен пользователь: {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto,
                              @PathVariable @Positive long id) {
        log.info("Обновление id = {} пользователя: {}", id, userDto);
        return userClient.updateUser(userDto, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive long id) {
        log.info("Получен запрос на получение пользователя {}", id);
        return userClient.getUser(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive long id) {
        log.info("Получен запрос на удаление пользователя {}", id);
        return userClient.deleteUser(id);
    }
}
