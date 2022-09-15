package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class DbUserService implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Autowired
    public DbUserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto == null || !checkId(userDto)) {
            throw new ValidationException("Невозможно создать пользователя с существующим id");
        }

        User user = userRepository.save(userMapper.toUser(userDto));
        log.info("Пользователь создан: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        if (!userRepository.existsById(id)) {
            throw new ValidationException("Невозможно обновить пользователя не верный формат id");
        }

        userDto.setId(id);

        User userToUpdate = userMapper.toUser(userDto);
        User userFromDb = getUserFromOptional(userRepository.findById(id));

        if (userToUpdate.getName() == null) {
            userToUpdate.setName(userFromDb.getName());
        }

        if (userToUpdate.getEmail() == null) {
            userToUpdate.setEmail(userFromDb.getEmail());
        }

        User user = userRepository.save(userToUpdate);
        log.info("Пользователь обновлен: {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return userMapper.toUserDto(getUserFromOptional(userRepository.findById(id)));
    }

    @Override
    public boolean deleteUser(long id) {
        userRepository.deleteById(id);
        return true;
    }

    private boolean checkId(UserDto userDto) {
        return userDto.getId() == 0 && !userRepository.existsById(userDto.getId());
    }

    private User getUserFromOptional(Optional<User> optionalUser) {
        return optionalUser.orElseThrow(() -> new StorageException("Ошибка получения пользователя"));
    }
}
