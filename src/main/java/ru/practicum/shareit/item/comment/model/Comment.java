package ru.practicum.shareit.item.comment.model;

import lombok.AllArgsConstructor;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "text")
    String text;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    User author;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    Item item;

    @Column(name = "creation_date")
    LocalDateTime creationDate;
}
