package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    ItemDto getItem(long id);

    List<ItemDto> getAllUserItems(long userId);

    List<ItemDto> searchItems(String name);
}
