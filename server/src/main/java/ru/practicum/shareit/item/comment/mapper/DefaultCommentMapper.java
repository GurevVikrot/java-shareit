package ru.practicum.shareit.item.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

import java.time.LocalDateTime;

@Component
public class DefaultCommentMapper implements CommentMapper {
    @Override
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreationDate());
    }

    @Override
    public Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getId(),
                commentDto.getText().trim(),
                null,
                null,
                LocalDateTime.now());
    }
}
