package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookings;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * // TODO .
 */

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получена для создания вещь: {}, \n owner = {}", itemDto, userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable @Positive long id,
                              @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получена для обновления вещь: {}, \n owner = {}", itemDto, userId);
        return itemService.updateItem(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ItemDtoBookings getItem(@PathVariable @Positive long id,
                           @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен запрос на вещь id = {}", id);
        return itemService.getItem(id, userId);
    }

    @GetMapping
    public List<ItemDtoBookings> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен запрос все вещей пользователя id = {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи = {}", text);
        return itemService.searchItems(text);
    }
}
