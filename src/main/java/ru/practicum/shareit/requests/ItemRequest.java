package ru.practicum.shareit.requests;

import lombok.Data;

import java.time.LocalDate;

/**
 * // TODO .
 */

@Data
public class ItemRequest {
    private long requestId;
    private String name;
    private String description;
    private long creatorId;
    private LocalDate crated;
    private boolean isDone = false;
}