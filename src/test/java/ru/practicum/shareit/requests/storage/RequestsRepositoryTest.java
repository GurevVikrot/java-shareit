package ru.practicum.shareit.requests.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestsRepositoryTest {
    private final Sort sort = Sort.by(Sort.Direction.DESC, "created");
    private LocalDateTime now;
    private User user;
    private User requester;
    private Item item;
    private ItemRequest itemRequest;


    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestsRepository requestsRepository;

    @BeforeEach
    void beforeEach() {
        now = LocalDateTime.now();
        user = new User(null, "Vitya", "vitya@mail.ru");
        requester = new User(null, "Vova", "vova@mail.ru");
        em.persist(user);
        em.persist(requester);
    }

    @Test
    void findAllByRequester_IdIsTest() {
        itemRequest = new ItemRequest(null, "Хочется", requester, null, now);
        ItemRequest itemRequest1 = new ItemRequest(null, "Не Хочется", requester, null, now.plusDays(1));
        ItemRequest itemRequest2 = new ItemRequest(null, "Очень Не Хочется", user, null, now.plusDays(2));
        item = new Item(null, "Вещь", "Супер", true, user, itemRequest);

        em.persist(itemRequest);
        em.persist(itemRequest1);
        em.persist(itemRequest2);
        em.persist(item);
        em.flush();
        em.clear();

        List<ItemRequest> requests = requestsRepository.findAllByRequester_IdIs(requester.getId(), sort);
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(itemRequest1.getId(), requests.get(0).getId());
        assertEquals(itemRequest1.getDescription(), requests.get(0).getDescription());
        assertEquals(requester.getId(), requests.get(0).getRequester().getId());
        assertEquals(requester.getName(), requests.get(0).getRequester().getName());
        assertTrue(requests.get(0).getItems().isEmpty());
        assertEquals(itemRequest1.getCreated().toLocalDate(), requests.get(0).getCreated().toLocalDate());
        assertEquals(itemRequest.getId(), requests.get(1).getId());
        assertEquals(itemRequest.getDescription(), requests.get(1).getDescription());
        assertEquals(requester.getId(), requests.get(1).getRequester().getId());
        assertEquals(requester.getName(), requests.get(1).getRequester().getName());
        assertEquals(item.getId(), requests.get(1).getItems().get(0).getId());
        assertEquals(item.getName(), requests.get(1).getItems().get(0).getName());
        assertEquals(item.getDescription(), requests.get(1).getItems().get(0).getDescription());
        assertEquals(itemRequest.getCreated().toLocalDate(), requests.get(1).getCreated().toLocalDate());
    }

    @Test
    void findAllByRequester_IdIsWhenNotRequesterTest() {
        itemRequest = new ItemRequest(null, "Хочется", requester, null, now);
        ItemRequest itemRequest1 = new ItemRequest(null, "Не Хочется", requester, null, now.plusDays(1));
        item = new Item(null, "Вещь", "Супер", true, user, itemRequest);

        em.persist(itemRequest);
        em.persist(itemRequest1);
        em.persist(item);
        em.flush();
        em.clear();

        List<ItemRequest> requests = requestsRepository.findAllByRequester_IdIs(user.getId(), sort);
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void findAllByRequester_IdNotTest() {
        itemRequest = new ItemRequest(null, "Хочется", requester, null, now);
        ItemRequest itemRequest1 = new ItemRequest(null, "Не Хочется", requester, null, now.plusDays(1));
        ItemRequest itemRequest2 = new ItemRequest(null, "Очень Не Хочется", user, null, now.plusDays(2));
        item = new Item(null, "Вещь", "Супер", true, user, itemRequest);

        em.persist(itemRequest);
        em.persist(itemRequest1);
        em.persist(itemRequest2);
        em.persist(item);
        em.flush();
        em.clear();

        List<ItemRequest> requests = requestsRepository
                .findAllByRequester_IdNot(user.getId(), PageRequest.of(0, 10, sort));
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(itemRequest1.getId(), requests.get(0).getId());
        assertEquals(itemRequest1.getDescription(), requests.get(0).getDescription());
        assertEquals(requester.getId(), requests.get(0).getRequester().getId());
        assertEquals(requester.getName(), requests.get(0).getRequester().getName());
        assertTrue(requests.get(0).getItems().isEmpty());
        assertEquals(itemRequest1.getCreated().toLocalDate(), requests.get(0).getCreated().toLocalDate());
        assertEquals(itemRequest.getId(), requests.get(1).getId());
        assertEquals(itemRequest.getDescription(), requests.get(1).getDescription());
        assertEquals(requester.getId(), requests.get(1).getRequester().getId());
        assertEquals(requester.getName(), requests.get(1).getRequester().getName());
        assertEquals(item.getId(), requests.get(1).getItems().get(0).getId());
        assertEquals(item.getName(), requests.get(1).getItems().get(0).getName());
        assertEquals(item.getDescription(), requests.get(1).getItems().get(0).getDescription());
        assertEquals(itemRequest.getCreated().toLocalDate(), requests.get(1).getCreated().toLocalDate());

        List<ItemRequest> requests1 = requestsRepository
                .findAllByRequester_IdNot(requester.getId(), PageRequest.of(0, 10, sort));
        assertNotNull(requests1);
        assertFalse(requests1.isEmpty());
        assertEquals(itemRequest2.getId(), requests1.get(0).getId());
        assertEquals(itemRequest2.getDescription(), requests1.get(0).getDescription());
        assertEquals(user.getId(), requests1.get(0).getRequester().getId());
        assertEquals(user.getName(), requests1.get(0).getRequester().getName());
        assertTrue(requests1.get(0).getItems().isEmpty());
        assertEquals(itemRequest2.getCreated().toLocalDate(), requests1.get(0).getCreated().toLocalDate());
    }
}