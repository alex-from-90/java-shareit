package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
//Более наглядный порядок аннотаций
@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String name;

    String description;

    boolean available;
    //Установили взаимосвязь
    @ManyToOne
    @JoinColumn(name = "owner_id")
    User user;

    @Column(name = "request_id")
    long requestId;
}