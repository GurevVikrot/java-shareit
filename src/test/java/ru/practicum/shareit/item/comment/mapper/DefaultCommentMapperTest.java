package ru.practicum.shareit.item.comment.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultCommentMapperTest {
    private static CommentMapper commentMapper;
    private CommentDto commentDto;
    private Comment comment;
    private LocalDateTime now = LocalDateTime.now();

    @BeforeAll
    public static void beforeAll() {
        commentMapper = new DefaultCommentMapper();
    }

    @BeforeEach
    void beforeEach() {
        commentDto = new CommentDto(0L, "Балдеж", null, null);
        comment = new Comment(
                1L,
                "Балдеж",
                new User(1L, "Vova", null),
                new Item(1L, "Вещь", null, null, null, null),
                now);
    }

    @Test
    void toCommentTestWithNullFields() {
        Comment comment1 = commentMapper.toComment(commentDto);

        assertEquals(commentDto.getId(), comment1.getId());
        assertEquals(commentDto.getText(), comment1.getText());
        assertNull(comment1.getAuthor());
        assertNull(comment1.getItem());
        assertNotNull(comment1.getCreationDate());
    }

    @Test
    void toCommentTestWithNotNullFields() {
        commentDto.setAuthorName("VVV");
        commentDto.setCreated(now.minusDays(2));
        Comment comment1 = commentMapper.toComment(commentDto);

        assertEquals(commentDto.getId(), comment1.getId());
        assertEquals(commentDto.getText(), comment1.getText());
        assertNull(comment1.getAuthor());
        assertNull(comment1.getItem());
        assertNotNull(comment1.getCreationDate());
        assertNotEquals(commentDto.getCreated(), comment1.getCreationDate());
    }

    @Test
    void toCommentDto() {
        CommentDto commentDto1 = commentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto1.getId());
        assertEquals(comment.getText(), commentDto1.getText());
        assertEquals(comment.getAuthor().getName(), commentDto1.getAuthorName());
        assertEquals(comment.getCreationDate(), commentDto1.getCreated());
    }
}