package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.exeption.StorageException;

@Component
public class ItemMapperDefault implements ItemMapper {
    private final UserStorage userStorage;
    private final RequestStorage requestStorage;

    @Autowired
    public ItemMapperDefault(UserStorage userStorage, RequestStorage requestStorage) {
        this.userStorage = userStorage;
        this.requestStorage = requestStorage;
    }

    @Override
    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null);
    }

    @Override
    public Item toItem(ItemDto itemDto, long userId) {
        User owner = userStorage.get(userId).orElseThrow(() -> new StorageException("Пользователя не существует"));

        return new Item(
                itemDto.getId(),
                itemDto.getName() != null ? itemDto.getName().trim() : null,
                itemDto.getDescription() != null ? itemDto.getDescription().trim() : null,
                itemDto.getAvailable(),
                owner,
                itemDto.getRequest());
    }
}
