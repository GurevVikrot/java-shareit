package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookings;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapperDefault implements ItemMapper {

    @Override
    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName().trim(),
                item.getDescription().trim(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    @Override
    public Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName() != null ? itemDto.getName().trim() : null,
                itemDto.getDescription() != null ? itemDto.getDescription().trim() : null,
                itemDto.getAvailable(),
                null,
                null);
    }

    @Override
    public ItemDtoBookings toItemBookingDto(Item item) {
        return new ItemDtoBookings(item.getId(),
                item.getName().trim(),
                item.getDescription().trim(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                null,
                null,
                null);
    }
}
