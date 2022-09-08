package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * // TODO .
 */

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto postRequest(@RequestBody @Valid ItemRequestDto requestDto,
                                      @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Попытка добавления запроса на вещь {}", requestDto);
        return requestService.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnerRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получение всех запросов на вещи пользователя {}", userId);
        return requestService.getOwnerRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero int from,
                                               @RequestParam(required = false, defaultValue = "10")
                                               @Positive int size,
                                               @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получение всех запросов на вещи начиная с {}\n " +
                "Элементов в странице {}", from, size);
        return requestService.getAllRequestPagination(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable @Positive long requestId,
                                     @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получение запроса на вещь id = {} пользователем id = {}", requestId, userId);
        return requestService.getRequest(requestId, userId);
    }
}
