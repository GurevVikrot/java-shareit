package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findItemsTest() {
        User user = new User(null, "Vitya", "vitya@mail.ru");
        Item item1 = new Item(null, "Вещь", "Супер", true, user, null);
        Item item2 = new Item(null, "Cупер", "Вещь то супер", true, user, null);
        Item item3 = new Item(null, "Чевапчич", "с гнильцой", true, user, null);
        Item item4 = new Item(null, "Вещь", "Супер", false, user, null);

        em.persist(user);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);

        List<Item> itemsFromBd = itemRepository.find("вещь", PageRequest.of(0, 3));
        assertEquals(List.of(item1, item2, item4), itemsFromBd);

        itemsFromBd = itemRepository.find("вещь", PageRequest.of(0, 1));
        assertEquals(List.of(item1), itemsFromBd);

        itemsFromBd = itemRepository.find("вещь", PageRequest.of(1, 3));
        assertEquals(List.of(), itemsFromBd);

        itemsFromBd = itemRepository.find("вещь", PageRequest.of(1, 1));
        assertEquals(List.of(item2), itemsFromBd);

        itemsFromBd = itemRepository.find("ваПч", PageRequest.of(0, 1));
        assertEquals(List.of(item3), itemsFromBd);
    }

    @Test
    void findAllByOwnerIdTest() {
        User user = new User(null, "Vitya", "vitya@mail.ru");
        Item item1 = new Item(null, "Вещь", "Супер", true, user, null);
        Item item2 = new Item(null, "Cупер", "Вещь то супер", true, user, null);
        Item item3 = new Item(null, "Чевапчич", "с гнильцой", true, user, null);
        Item item5 = new Item(null, "Вещь", "Супер", false, user, null);
        em.persist(user);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(item5);

        User user2 = new User(null, "Vova", "vova@mail.ru");
        Item item4 = new Item(null, "Вещь", "Супер", true, user2, null);
        em.persist(user2);
        em.persist(item4);

        List<Item> itemsFromBd = itemRepository.findAllByOwner_IdOrderById(user.getId(), PageRequest.of(0, 3));
        assertEquals(List.of(item1, item2, item3), itemsFromBd);

        itemsFromBd = itemRepository.findAllByOwner_IdOrderById(user2.getId(), PageRequest.of(0, 3));
        assertEquals(List.of(item4), itemsFromBd);

        itemsFromBd = itemRepository.findAllByOwner_IdOrderById(99, PageRequest.of(0, 3));
        assertTrue(itemsFromBd.isEmpty());
    }
}