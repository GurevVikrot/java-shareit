package ru.practicum.shareit.requests.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.stream.Collectors;

@Component
public class DefaultRequestMapper implements RequestMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public DefaultRequestMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(null,
                itemRequestDto.getDescription().trim(),
                null,
                null,
                null);
    }

    @Override
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                userMapper.toUserDto(itemRequest.getRequester()),
                itemRequest.getItems() != null ? itemRequest.getItems().stream()
                        .map(itemMapper::toItemDto)
                        .collect(Collectors.toList()) : null,
                itemRequest.getCreated());
    }
}
