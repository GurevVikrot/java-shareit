package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.DbUserService;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationDbItemServiceTest {
    private final DbItemService itemService;
    private final DbUserService userService;
    private final ItemRepository itemRepository;
    private final UserDto user = new UserDto(0L, "Vitya", "Vitya@mail.ru");
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(0L, "Вещь", "Супер", true, null);
    }

    @Test
    void updateItemTest() {
        userService.createUser(user);
        itemService.createItem(itemDto, 1L);

        itemDto.setName("Up");
        itemDto.setDescription("Date");
        itemDto.setAvailable(false);

        ItemDto itemDtoUpdated = itemService.updateItem(itemDto, 1L, 1L);
        itemDto.setId(1);

        assertEquals(itemDto, itemDtoUpdated);

        Optional<Item> optionalItem = itemRepository.findById(1L);
        AssertionsForClassTypes.assertThat(optionalItem).isPresent();
        Item itemFromDB = optionalItem.orElseThrow();
        user.setId(1L);

        assertThat(itemFromDB.getId(), notNullValue());
        assertThat(itemFromDB.getId(), equalTo(1L));
        assertThat(itemFromDB.getName(), notNullValue());
        assertThat(itemFromDB.getName(), equalTo(itemDto.getName()));
        assertThat(itemFromDB.getDescription(), notNullValue());
        assertThat(itemFromDB.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemFromDB.getAvailable(), notNullValue());
        assertThat(itemFromDB.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemFromDB.getOwner(), notNullValue());
        assertThat(itemFromDB.getOwner(), equalTo(new User(1L, "Vitya", "Vitya@mail.ru")));
        assertThat(itemFromDB.getRequest(), nullValue());
    }

    @Test
    void updateWhenItemNotExist() {
        userService.createUser(user);

        itemDto.setName("Up");
        itemDto.setDescription("Date");

        assertThrows(StorageException.class, () -> itemService.updateItem(itemDto, 1L, 1L));
    }

    @Test
    void updateWhenNotOwner() {
        userService.createUser(user);
        user.setEmail("vavaviva@Mail.ru");
        userService.createUser(user);
        itemService.createItem(itemDto, 1L);

        itemDto.setName("Up");
        itemDto.setDescription("Date");

        assertThrows(StorageException.class, () -> itemService.updateItem(itemDto, 1L, 2L));
    }

    @Test
    void updateWithNullFields() {
        userService.createUser(user);
        itemService.createItem(itemDto, 1L);

        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);

        ItemDto updatedItem = itemService.updateItem(itemDto, 1L, 1L);

        itemDto.setId(1L);
        itemDto.setName("Вещь");
        itemDto.setDescription("Супер");
        itemDto.setAvailable(true);
        assertEquals(itemDto, updatedItem);

        Optional<Item> optionalItem = itemRepository.findById(1L);
        AssertionsForClassTypes.assertThat(optionalItem).isPresent();
        Item itemFromDB = optionalItem.orElseThrow();

        assertThat(itemFromDB.getId(), notNullValue());
        assertThat(itemFromDB.getId(), equalTo(1L));
        assertThat(itemFromDB.getName(), notNullValue());
        assertThat(itemFromDB.getName(), equalTo(itemDto.getName()));
        assertThat(itemFromDB.getDescription(), notNullValue());
        assertThat(itemFromDB.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemFromDB.getAvailable(), notNullValue());
        assertThat(itemFromDB.getAvailable(), equalTo(true));
        assertThat(itemFromDB.getOwner(), notNullValue());
        assertThat(itemFromDB.getOwner(), equalTo(new User(1L, "Vitya", "Vitya@mail.ru")));
        assertThat(itemFromDB.getRequest(), nullValue());
    }
}