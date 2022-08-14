package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookings;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    ItemDtoBookings getItem(long id, long userId);

    List<ItemDtoBookings> getAllUserItems(long userId);

    List<ItemDto> searchItems(String name);
}
