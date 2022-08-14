package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exeption.StorageException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class OptionalTaker {
    public static User getUser(Optional<User> optionalUser) {
        return optionalUser.orElseThrow(() -> new StorageException("Ошибка получения пользователя"));
    }

    public static Item getItem(Optional<Item> itemOptional) {
        return itemOptional.orElseThrow(() -> new StorageException("Ошибка получения вещи"));
    }

    public static Booking getBooking(Optional<Booking> bookingOptional) {
        return bookingOptional.orElseThrow(() -> new StorageException("Ошибка получения бронирования"));
    }
}
