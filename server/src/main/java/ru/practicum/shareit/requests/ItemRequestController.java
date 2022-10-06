package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto postRequest(@RequestBody ItemRequestDto requestDto,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Попытка добавления запроса на вещь {}", requestDto);
        return requestService.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnerRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение всех запросов на вещи пользователя {}", userId);
        return requestService.getOwnerRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "10") int size,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение всех запросов на вещи начиная с {}\n " +
                "Элементов в странице {}", from, size);
        return requestService.getAllRequestPagination(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable long requestId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение запроса на вещь id = {} пользователем id = {}", requestId, userId);
        return requestService.getRequest(requestId, userId);
    }
}
