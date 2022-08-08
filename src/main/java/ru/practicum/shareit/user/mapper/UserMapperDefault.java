package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
@Validated
public class UserMapperDefault implements UserMapper {
    @Override
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public User toUser(UserDto user) {
        return new User(
                user.getId(),
                user.getName() != null ? user.getName().trim() : null,
                user.getEmail() != null ? user.getEmail().trim() : null
        );
    }
}
