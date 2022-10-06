package ru.practicum.shareit.requests.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultRequestMapperTest {
    private final UserDto requesterDto = new UserDto(2L, "Vova", "vova@mail.ru");
    private final User requester = new User(2L, "Vova", "vova@mail.ru");
    private final User user = new User(1L, "Vitya", "vitya@mail.ru");
    private final ItemDto itemDto = new ItemDto(1L, "Вещь", "Супер", true, 1L);
    private LocalDateTime now;
    private ItemRequest itemRequest;
    private final Item item = new Item(1L, "Вещь", "Супер", true, user, itemRequest);
    private ItemRequestDto itemRequestDto;
    @Mock
    private ItemMapper mokItemMapper;

    @Mock
    private UserMapper mokUserMapper;

    @InjectMocks
    private DefaultRequestMapper defaultRequestMapper;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        itemRequest = new ItemRequest(1L, "Хочется", requester, List.of(item), now);
        itemRequestDto = new ItemRequestDto(0L, "Хочется", null, null, null);
    }

    @Test
    void toItemRequestTest() {
        ItemRequest mappedItemRequest = defaultRequestMapper.toItemRequest(itemRequestDto);

        assertNotNull(mappedItemRequest);
        assertNull(mappedItemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), mappedItemRequest.getDescription());
        assertNull(mappedItemRequest.getRequester());
        assertNull(mappedItemRequest.getItems());
        assertNull(mappedItemRequest.getCreated());
    }

    @Test
    void toItemRequestWithNotNullValuesTest() {
        itemRequestDto.setRequester(requesterDto);
        itemRequestDto.setItems(List.of(itemDto));
        itemRequestDto.setCreated(now);

        ItemRequest mappedItemRequest = defaultRequestMapper.toItemRequest(itemRequestDto);

        assertNotNull(mappedItemRequest);
        assertNull(mappedItemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), mappedItemRequest.getDescription());
        assertNull(mappedItemRequest.getRequester());
        assertNull(mappedItemRequest.getItems());
        assertNull(mappedItemRequest.getCreated());
    }

    @Test
    void toItemRequestTrimDescriptionTest() {
        itemRequestDto.setDescription(" Хочется  ");
        ItemRequest mappedItemRequest = defaultRequestMapper.toItemRequest(itemRequestDto);

        assertNotNull(mappedItemRequest);
        assertEquals(itemRequestDto.getDescription().trim(), mappedItemRequest.getDescription());


        itemRequestDto.setDescription(" ");
        mappedItemRequest = defaultRequestMapper.toItemRequest(itemRequestDto);

        assertNotNull(mappedItemRequest);
        assertEquals(itemRequestDto.getDescription().trim(), mappedItemRequest.getDescription());

        itemRequestDto.setDescription("");

        mappedItemRequest = defaultRequestMapper.toItemRequest(itemRequestDto);

        assertNotNull(mappedItemRequest);
        assertEquals(itemRequestDto.getDescription(), mappedItemRequest.getDescription());

        itemRequestDto.setDescription(null);
        assertThrows(NullPointerException.class, () ->
                defaultRequestMapper.toItemRequest(itemRequestDto));
    }

    @Test
    void toItemRequestDtoTest() {
        Mockito
                .when(mokUserMapper.toUserDto(requester))
                .thenReturn(requesterDto);

        Mockito
                .when(mokItemMapper.toItemDto(item))
                .thenReturn(itemDto);

        ItemRequestDto mappedItemRequestDto = defaultRequestMapper.toItemRequestDto(itemRequest);

        assertNotNull(mappedItemRequestDto);
        assertEquals(itemRequest.getId(), mappedItemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), mappedItemRequestDto.getDescription());
        assertEquals(requesterDto, mappedItemRequestDto.getRequester());
        assertEquals(1, mappedItemRequestDto.getItems().size());
        assertEquals(itemDto, mappedItemRequestDto.getItems().get(0));
        assertEquals(now, mappedItemRequestDto.getCreated());
    }

    @Test
    void toItemRequestDtoWhenNoItemsNullValueTest() {
        Mockito
                .when(mokUserMapper.toUserDto(requester))
                .thenReturn(requesterDto);

        itemRequest.setItems(null);
        ItemRequestDto mappedItemRequestDto = defaultRequestMapper.toItemRequestDto(itemRequest);

        assertNotNull(mappedItemRequestDto);
        assertEquals(itemRequest.getId(), mappedItemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), mappedItemRequestDto.getDescription());
        assertEquals(requesterDto, mappedItemRequestDto.getRequester());
        assertNull(mappedItemRequestDto.getItems());
        assertEquals(now, mappedItemRequestDto.getCreated());
    }
}