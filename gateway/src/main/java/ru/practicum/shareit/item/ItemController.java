package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получена для создания вещь: {}, \n owner = {}", itemDto, userId);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable @Positive long id,
                                             @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получена для обновления вещь: {}, \n owner = {}", itemDto, userId);
        return itemClient.updateItem(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable @Positive long id,
                                          @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен запрос на вещь id = {}", id);
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestParam(required = false, defaultValue = "0")
                                                  @PositiveOrZero int from,
                                                  @RequestParam(required = false, defaultValue = "10")
                                                  @Positive int size,
                                                  @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен запрос все вещей пользователя id = {}", userId);
        return itemClient.getAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero int from,
                                               @RequestParam(required = false, defaultValue = "10")
                                               @Positive int size,
                                               @RequestParam @NotNull @NotBlank String text,
                                               @RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "1") long userId) {
        log.info("Получен запрос на поиск вещи = {}", text);
        return itemClient.searchItems(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@PathVariable @Positive long itemId,
                                              @RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                              @RequestBody @Valid CommentDto commentDto) {
        log.info("Запрос добавления комментария от userId {} к itemId {}", userId, itemId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
