package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.RequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.RequestsRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DbRequestServiceTest {
    private final UserDto requesterDto = new UserDto(2L, "Vova", "vova@mail.ru");
    private final User requester = new User(2L, "Vova", "vova@mail.ru");
    private final User user = new User(1L, "Vitya", "vitya@mail.ru");
    private final ItemDto itemDto = new ItemDto(1L, "Вещь", "Супер", true, 1L);
    private LocalDateTime now;
    private ItemRequest itemRequestFromDb;
    private final Item item = new Item(1L, "Вещь", "Супер", true, user, itemRequestFromDb);
    private ItemRequest itemRequestFromMapper;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDtoFromMapper;

    @Mock
    private UserRepository mokUserRepository;

    @Mock
    private RequestsRepository mokRequestsRepository;

    @Mock
    private RequestMapper mokRequestMapper;

    @InjectMocks
    private DbRequestService requestService;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        itemRequestFromMapper = new ItemRequest(0L, "Хочется", null, null, null);
        itemRequestFromDb = new ItemRequest(1L, "Хочется", requester, List.of(item), now);
        itemRequestDto = new ItemRequestDto(0L, "Хочется", null, null, null);
        itemRequestDtoFromMapper = new ItemRequestDto(1L, "Хочется", requesterDto, List.of(itemDto), now);
    }

    @Test
    void createRequestTest() {
        Mockito
                .when(mokRequestMapper.toItemRequest(Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemRequestFromMapper);

        Mockito
                .when(mokUserRepository.findById(2L))
                .thenReturn(Optional.of(requester));

        Mockito
                .when(mokRequestsRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequestFromDb);

        Mockito
                .when(mokRequestMapper.toItemRequestDto(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequestDtoFromMapper);

        ItemRequestDto mappedItemRequestDto = requestService.createRequest(itemRequestDto, 2L);
        assertNotNull(mappedItemRequestDto);
        assertEquals(itemRequestDtoFromMapper.getId(), mappedItemRequestDto.getId());
        assertEquals(itemRequestDtoFromMapper.getDescription(), mappedItemRequestDto.getDescription());
        assertEquals(requesterDto, mappedItemRequestDto.getRequester());
        assertEquals(1, mappedItemRequestDto.getItems().size());
        assertEquals(itemDto, mappedItemRequestDto.getItems().get(0));
        assertEquals(now, mappedItemRequestDto.getCreated());

        Mockito.verify(mokUserRepository, Mockito.times(1))
                .findById(2L);

        Mockito.verify(mokRequestsRepository, Mockito.times(1))
                .save(Mockito.any(ItemRequest.class));

        Mockito.verify(mokRequestMapper, Mockito.times(1))
                .toItemRequest(Mockito.any(ItemRequestDto.class));

        Mockito.verify(mokRequestMapper, Mockito.times(1))
                .toItemRequestDto(Mockito.any(ItemRequest.class));
    }

    @Test
    void createRequestWhenRequesterNotExistTest() {
        Mockito
                .when(mokRequestMapper.toItemRequest(Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemRequestFromMapper);

        Mockito
                .when(mokUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(StorageException.class, () -> requestService.createRequest(itemRequestDto, 2L));
    }

    @Test
    void getOwnerRequestsTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        Mockito
                .when(mokRequestsRepository.findAllByRequester_IdIs(Mockito.anyLong(), Mockito.any(Sort.class)))
                .thenReturn(List.of(itemRequestFromDb));

        Mockito
                .when(mokRequestMapper.toItemRequestDto(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequestDtoFromMapper);

        List<ItemRequestDto> requests = requestService.getOwnerRequests(2L);
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(itemRequestDtoFromMapper, requests.get(0));

        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);

        Mockito.verify(mokRequestsRepository, Mockito.times(1))
                .findAllByRequester_IdIs(Mockito.anyLong(), Mockito.any(Sort.class));
    }

    @Test
    void getOwnerRequestsWhenNotHaveRequestsTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        Mockito
                .when(mokRequestsRepository.findAllByRequester_IdIs(Mockito.anyLong(), Mockito.any(Sort.class)))
                .thenReturn(List.of());

        List<ItemRequestDto> requests = requestService.getOwnerRequests(2L);
        assertNotNull(requests);
        assertTrue(requests.isEmpty());

        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);

        Mockito.verify(mokRequestsRepository, Mockito.times(1))
                .findAllByRequester_IdIs(Mockito.anyLong(), Mockito.any(Sort.class));
    }

    @Test
    void getOwnerRequestsWhenRequesterNotExistTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> requestService.getOwnerRequests(2L));

        Mockito.verify(mokRequestsRepository, Mockito.never())
                .findAllByRequester_IdIs(Mockito.anyLong(), Mockito.any(Sort.class));
    }

    @Test
    void getAllRequestPaginationTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        Mockito
                .when(mokRequestsRepository.findAllByRequester_IdNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(itemRequestFromDb));

        Mockito
                .when(mokRequestMapper.toItemRequestDto(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequestDtoFromMapper);

        List<ItemRequestDto> requests = requestService.getAllRequestPagination(0, 10, 2L);
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(itemRequestDtoFromMapper, requests.get(0));

        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);

        Mockito.verify(mokRequestsRepository, Mockito.times(1))
                .findAllByRequester_IdNot(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getAllRequestPaginationWhenUserNotExistTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> requestService.getAllRequestPagination(0, 10, 2L));

        Mockito.verify(mokRequestsRepository, Mockito.never())
                .findAllByRequester_IdNot(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getAllRequestPaginationWhenNotHaveRequestsTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        Mockito
                .when(mokRequestsRepository.findAllByRequester_IdNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of());

        List<ItemRequestDto> requests = requestService.getAllRequestPagination(0, 10, 2L);
        assertNotNull(requests);
        assertTrue(requests.isEmpty());

        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);

        Mockito.verify(mokRequestsRepository, Mockito.times(1))
                .findAllByRequester_IdNot(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getRequestTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        Mockito
                .when(mokRequestsRepository.findById(1L))
                .thenReturn(Optional.of(itemRequestFromDb));

        Mockito
                .when(mokRequestMapper.toItemRequestDto(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequestDtoFromMapper);

        assertEquals(itemRequestDtoFromMapper, requestService.getRequest(1L, 2L));

        Mockito.verify(mokUserRepository, Mockito.times(1))
                .existsById(2L);

        Mockito.verify(mokRequestsRepository, Mockito.times(1))
                .findById(1L);
    }

    @Test
    void getRequestWhenUserNotExistTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(false);

        assertThrows(StorageException.class, () -> requestService.getRequest(1L, 2L));

        Mockito.verify(mokRequestsRepository, Mockito.never())
                .findById(Mockito.anyLong());
    }

    @Test
    void getRequestWhenRequestNotExistTest() {
        Mockito
                .when(mokUserRepository.existsById(2L))
                .thenReturn(true);

        Mockito
                .when(mokRequestsRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(StorageException.class, () -> requestService.getRequest(1L, 2L));
    }
}