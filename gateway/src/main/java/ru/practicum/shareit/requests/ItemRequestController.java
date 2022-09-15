package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestBody @Valid ItemRequestDto requestDto,
                                              @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Попытка добавления запроса на вещь {}", requestDto);
        return itemRequestClient.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получение всех запросов на вещи пользователя {}", userId);
        return itemRequestClient.getOwnerRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero int from,
                                               @RequestParam(required = false, defaultValue = "10")
                                               @Positive int size,
                                               @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получение всех запросов на вещи начиная с {}\n " +
                "Элементов в странице {}", from, size);
        return itemRequestClient.getAllRequestPagination(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable @Positive long requestId,
                                     @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получение запроса на вещь id = {} пользователем id = {}", requestId, userId);
        return itemRequestClient.getRequest(requestId, userId);
    }
}
