package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(ItemRequestDto requestDto, long userId);

    List<ItemRequestDto> getOwnerRequests(long userId);

    List<ItemRequestDto> getAllRequestPagination(int from, int size, long userId);

    ItemRequestDto getRequest(long requestId, long userId);
}
