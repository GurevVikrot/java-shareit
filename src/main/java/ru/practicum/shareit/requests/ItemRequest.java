package ru.practicum.shareit.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * // TODO .
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long requestId;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "user_id")
    private User requester;

    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    private Item item;
}