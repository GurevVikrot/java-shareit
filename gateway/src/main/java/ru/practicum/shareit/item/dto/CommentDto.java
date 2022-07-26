package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private long id;

    @NotBlank
    @Size(max = 521)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
