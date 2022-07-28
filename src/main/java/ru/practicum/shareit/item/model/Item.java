package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * // TODO .
 */

@Data
@AllArgsConstructor
public class Item {
    private long id;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotBlank
    @Size(max = 300)
    private String description;
    private Boolean available;
    private final User owner;
    // Если вещь добавлена по запросу другого пользователя, указываем запрос
    private final ItemRequest request;
}
