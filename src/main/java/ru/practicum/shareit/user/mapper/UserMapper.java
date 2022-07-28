package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserMapper {
    UserDto toUserDto(User user);
    User toUser(UserDto user);
}
