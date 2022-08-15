package ru.practicum.shareit.item.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

@Component
public class DefaultCommentMapper implements CommentMapper {
    @Override
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreationDate());
    }
}
