package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.DbItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.RequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationDbRequestServiceTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestsRepository requestsRepository;
    private final DbItemService itemService;
    private final DbRequestService requestService;
    private final EntityManager entityManager;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(0L, "Хочется", null, null, null);
    private final User user = new User(null, "Vitya", "vitya@mail.ru");
    private final User requester = new User(null, "Vova", "vova@mail.ru");
    private final ItemDto itemDto = new ItemDto(0L, "Вещь", "Супер", true, 1L);
    private LocalDateTime now;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void beforeEach() {
        userRepository.save(user);
        userRepository.save(requester);
        now = LocalDateTime.now();
        itemRequest = new ItemRequest(null, "Хочется", requester, null, now);
        requestsRepository.save(itemRequest);
        item = new Item(1L, "Вещь", "Супер", true, user, itemRequest);
        itemRepository.save(item);
        // Завершение транзакции, чтобы hibernate брал сущности не из кэша
        entityManager.clear();
    }

    @Test
    void getOwnerRequestTest() {
        List<ItemRequestDto> requests = requestService.getOwnerRequests(2L);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(1L, requests.get(0).getId());
        assertEquals(itemRequest.getDescription(), requests.get(0).getDescription());
        assertEquals(2L, requests.get(0).getRequester().getId());
        assertEquals(requester.getName(), requests.get(0).getRequester().getName());
        assertEquals(1L, requests.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), requests.get(0).getItems().get(0).getName());
        assertEquals(1L, requests.get(0).getItems().get(0).getRequestId());
        assertEquals(now.toLocalDate(), requests.get(0).getCreated().toLocalDate());
    }

    @Test
    void getOwnerRequestWhenNotHaveRequestsAndNotOwnerTest() {
        List<ItemRequestDto> requests = requestService.getOwnerRequests(1L);

        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void getOwnerRequestWhenUserNotExistTest() {
        assertThrows(StorageException.class, () -> requestService.getOwnerRequests(5L));
    }
}