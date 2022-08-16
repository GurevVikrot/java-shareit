package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    @Positive
    private long id;

    @NotBlank
    @Size(max = 521)
    private String text;

    @NotBlank
    private String authorName;

    @Past
    private LocalDateTime created;
}
