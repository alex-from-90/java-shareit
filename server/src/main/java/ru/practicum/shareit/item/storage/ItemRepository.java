package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text, Pageable pageable);

    @Query(" select i from Item i " +
            "where i.user.id = ?1")
    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("select i from Item i where i.itemRequest.requester.id = ?1")
    List<Item> findAllByRequesterId(Long requesterId);

    @Query("select i from Item i where i.itemRequest.requester.id <> ?1")
    List<Item> findAllByRequesterIdIsNot(Long requesterId);

    @Query("select i from Item i where i.itemRequest.id = ?1")
    List<Item> findAllByRequestId(Long requestId);

    @Override
    Page<Item> findAll(Pageable pageable);
}

