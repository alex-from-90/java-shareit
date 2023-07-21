package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotBlank
    @NotNull
    String text;

    @CreatedDate
    LocalDateTime created;

    @ManyToOne // Объявляем связь "многие-к-одному" (множество комментариев к одному элементу)
    @JoinColumn(name = "item_id") // Указываем имя колонки в базе данных для хранения идентификатора элемента
    private Item item; // Связь с сущностью Item

    @Column(name = "author_id")
    long authorId;
    String authorName;
}