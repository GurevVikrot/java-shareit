package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperDefaultTest {
    private static ItemMapperDefault itemMapper;
    private final User user = new User(1L, "Vitya", "vitya@mail.ru");
    private Item item;
    private ItemDto itemDto;

    @BeforeAll
    static void setUp() {
        itemMapper = new ItemMapperDefault();
    }

    @BeforeEach
    void beforeEach() {
        item = new Item(1L, "Вещь", "Супер", true, user, null);
        itemDto = new ItemDto(1L, "Вещь", "Супер", true, null);
    }

    @Test
    void toItemDtoTest() {
        item.setName(" Вещь  ");
        item.setDescription(" Супер ");
        ItemDto itemDto1 = itemMapper.toItemDto(item);

        assertEquals(1L, itemDto1.getId());
        assertEquals("Вещь", itemDto1.getName());
        assertEquals("Супер", itemDto1.getDescription());
        assertTrue(itemDto1.getAvailable());
        assertNull(itemDto1.getRequestId());
    }

    @Test
    void toItemDtoWithRequest() {
        ItemRequest itemRequest = new ItemRequest(1L, "Хочется", null, null, null);
        item.setRequest(itemRequest);
        ItemDto itemDto1 = itemMapper.toItemDto(item);
        assertNotNull(itemDto1.getRequestId());
        assertEquals(1L, itemDto1.getRequestId());
    }

    @Test
    void toItemTest() {
        itemDto.setName(" Вещь  ");
        itemDto.setDescription(" Супер ");
        Item item1 = itemMapper.toItem(itemDto);

        assertEquals(1L, item1.getId());
        assertEquals("Вещь", item1.getName());
        assertEquals("Супер", item1.getDescription());
        assertTrue(item1.getAvailable());
        assertNull(item1.getOwner());
        assertNull(item1.getRequest());
    }

    @Test
    void toItemWithNullFields() {
        itemDto.setName(null);
        itemDto.setDescription(null);
        Item item1 = itemMapper.toItem(itemDto);

        assertEquals(1L, item1.getId());
        assertNull(item1.getName());
        assertNull(item1.getDescription());
        assertTrue(item1.getAvailable());
        assertNull(item1.getOwner());
        assertNull(item1.getRequest());
    }

    @Test
    void toItemBookingDtoTest() {
        item.setName(" Вещь  ");
        item.setDescription(" Супер ");
        ItemRequest itemRequest = new ItemRequest(1L, "Хочется", null, null, null);
        item.setRequest(itemRequest);

        ItemDtoBookings itemDtoBookings1 = itemMapper.toItemBookingDto(item);

        assertEquals(1L, itemDtoBookings1.getId());
        assertEquals("Вещь", itemDtoBookings1.getName());
        assertEquals("Супер", itemDtoBookings1.getDescription());
        assertTrue(itemDtoBookings1.getAvailable());
        assertEquals(itemRequest, itemDtoBookings1.getRequest());
        assertNull(itemDtoBookings1.getLastBooking());
        assertNull(itemDtoBookings1.getNextBooking());
        assertNull(itemDtoBookings1.getComments());
    }

    @Test
    void toItemBookingDtoWithNullFields() {
        ItemDtoBookings itemDtoBookings1 = itemMapper.toItemBookingDto(item);
        assertNull(itemDtoBookings1.getRequest());
    }
}