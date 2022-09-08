package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * // TODO .
 */

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotBlank
    @Size(max = 300)
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
