package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.exeption.StorageException;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultUserService implements UserService {
    private final UserMapper userMapper;
    private final UserStorage userStorage;

    @Autowired
    public DefaultUserService(UserMapper userMapper, UserStorage userStorage) {
        this.userMapper = userMapper;
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (!checkId(userDto)) {
            throw new ValidationException("Невозможно создать пользователя с существующим id");
        }

        User user = getFromOptional(userStorage.save(userMapper.toUser(userDto)));
        log.info("Пользователь создан: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        userDto.setId(id);

        if (checkId(userDto)) {
            throw new ValidationException("Невозможно обновить пользователя не верный формат id");
        }


        User user = getFromOptional(userStorage.update(userMapper.toUser(userDto)));
        log.info("Пользователь обновлен: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return userMapper.toUserDto(getFromOptional(userStorage.get(id)));
    }

    @Override
    public boolean deleteUser(long id) {
        if (userStorage.delete(id)) {
            log.info("Пользователь удален id = {}", id);
            return true;
        }
        throw new StorageException("Пользователя не существует");
    }

    private boolean checkId(UserDto userDto) {
        return userDto.getId() == 0 && !userStorage.userExist(userDto.getId());
    }

    private User getFromOptional(Optional<User> optionalUser) {
        return optionalUser.orElseThrow(() -> new StorageException("Ошибка получения пользователя"));
    }
}
