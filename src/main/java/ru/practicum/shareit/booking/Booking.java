package ru.practicum.shareit.booking;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

/**
 * // TODO .
 */
public class Booking {
    private long id;
    @NotNull
    @FutureOrPresent
    private LocalDate start;
    @NotNull
    @Future
    private LocalDate end;
    private long itemId;
    private long bookerId;
    private BookingStatus status;
}
