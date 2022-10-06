package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryTest {
    private User user;
    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        user = new User(0L, "Vitya", "vitya@mail.ru");
    }

    @Test
    void saveUserTest() {
        User userFromBd = userRepository.save(user);

        assertEquals(1L, userFromBd.getId());
        assertEquals("Vitya", userFromBd.getName());
        assertEquals("vitya@mail.ru", userFromBd.getEmail());

        user.setId(3L);
        user.setEmail("vova@mail.ru");
        User userFromBd2 = userRepository.save(user);

        assertEquals(2L, userFromBd2.getId());

        user.setId(0L);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user),
                "не соблюдается уникальность email");
    }

    @Test
    void saveUserWithNullFieldsTest() {
        user.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user),
                "Имя не может быть null");

        user.setName("Vitya");
        user.setEmail(null);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user),
                "email не может быть null");
    }
}