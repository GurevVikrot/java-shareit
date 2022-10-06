package ru.practicum.shareit.item.comment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "Vova", "vova@mail.ru");
        user2 = new User(null, "Vitya", "vitya@mail.ru");
        item1 = new Item(null, "Вещь", "Супер", true, user1, null);
        comment = new Comment(
                null,
                "Балдеж",
                user2,
                item1,
                LocalDateTime.now());
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(comment);
    }

    @Test
    void findAllByItemIdTest() {
        List<Comment> comments = commentRepository.findAllByItem_Id(item1.getId());

        assertEquals(1, comments.size());
        assertEquals(List.of(comment), comments);

        Comment comment2 = new Comment(
                null,
                "Балдеж",
                user2,
                item1,
                LocalDateTime.now());

        Comment comment3 = new Comment(
                null,
                "Балдеж",
                user2,
                item1,
                LocalDateTime.now());

        em.persist(comment2);
        em.persist(comment3);

        comments = commentRepository.findAllByItem_Id(item1.getId());

        assertEquals(3, comments.size());
        assertEquals(List.of(comment, comment2, comment3), comments);
    }
}