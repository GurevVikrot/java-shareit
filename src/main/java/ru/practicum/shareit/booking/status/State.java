package ru.practicum.shareit.booking.status;

import java.util.Optional;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<State> fromString(String stringState) {
        for (State s: values()) {
            if (stringState.equalsIgnoreCase(s.toString())) {
                return Optional.of(s);
            }
        }

        return Optional.empty();
    }
}
