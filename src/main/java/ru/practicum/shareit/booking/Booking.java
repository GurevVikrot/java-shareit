package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * // TODO .
 */

public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User taker;
    private BookingStatus status;
}
