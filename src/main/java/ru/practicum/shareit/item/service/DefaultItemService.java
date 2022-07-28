package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.util.StorageException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultItemService implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;

    @Autowired
    public DefaultItemService(ItemMapper itemMapper, ItemStorage itemStorage) {
        this.itemMapper = itemMapper;
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (!checkId(itemDto)) {
            throw new StorageException("Невозможно создать вещь, неверный формат id");
        }

        Item item = itemMapper.toItem(itemDto, userId);

        return itemMapper.toItemDto(getFromOptional(itemStorage.save(item)));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        itemDto.setId(id);

        if (checkId(itemDto)) {
            throw new StorageException("Невозможно обновить вещь, ее не существует");
        }

        Item item = itemMapper.toItem(itemDto, userId);

        return itemMapper.toItemDto(getFromOptional(itemStorage.update(item)));
    }

    @Override
    public ItemDto getItem(long id) {
        return itemMapper.toItemDto(getFromOptional(itemStorage.get(id)));
    }

    @Override
    public List<ItemDto> getAllUserItems(long userId) {
        return itemStorage.getAll().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getOwner().getId() == userId)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String name) {
        if (name.isEmpty()) {
            return new ArrayList<>();
        }

        return itemStorage.find(name.trim().toLowerCase()).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private boolean checkId(ItemDto itemDto) {
        return itemDto.getId() == 0 && !itemStorage.itemExist(itemDto.getId());
    }

    private Item getFromOptional(Optional<Item> itemOptional) {
        return itemOptional.orElseThrow(() -> new StorageException("Ошибка получения вещи"));
    }
}
