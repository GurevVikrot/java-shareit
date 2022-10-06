package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.RequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.RequestsRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbRequestService implements RequestService {
    private final UserRepository userRepository;
    private final RequestsRepository requestsRepository;
    private final RequestMapper requestMapper;

    public DbRequestService(UserRepository userRepository,
                            RequestsRepository requestsRepository,
                            RequestMapper requestMapper) {
        this.userRepository = userRepository;
        this.requestsRepository = requestsRepository;
        this.requestMapper = requestMapper;
    }

    @Override
    public ItemRequestDto createRequest(ItemRequestDto requestDto, long userId) {
        ItemRequest itemRequest = requestMapper.toItemRequest(requestDto);

        itemRequest.setRequester(userRepository.findById(userId)
                .orElseThrow(() -> new StorageException("Пользователя не существует")));
        itemRequest.setCreated(LocalDateTime.now());

        return requestMapper.toItemRequestDto(requestsRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnerRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new StorageException("Пользователя не существует");
        }

        return requestsRepository.findAllByRequester_IdIs(userId, getSort()).stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequestPagination(int from, int size, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new StorageException("Пользователя не существует");
        }

        Pageable pageable = PageRequest.of(from / size, size, getSort());

        return requestsRepository.findAllByRequester_IdNot(userId, pageable).stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(long requestId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new StorageException("Пользователя не существует");
        }

        return requestMapper.toItemRequestDto(requestsRepository.findById(requestId).orElseThrow(
                () -> new StorageException("Запроса на вещь не существует")));
    }

    private Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "created");
    }

}