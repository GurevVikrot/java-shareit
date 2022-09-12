package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoBookings {
    private long id;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotBlank
    @Size(max = 300)
    private String description;
    @NotNull
    private Boolean available;
    private ItemRequest request;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
