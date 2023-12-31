package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(ItemDto dto, long ownerId) {
        return post("", ownerId, dto);
    }

    public ResponseEntity<Object> patchItem(ItemDto dto, long ownerId, long itemId) {
        return patch("/" + itemId, ownerId, dto);
    }

    public ResponseEntity<Object> getItem(long itemId, long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getAllItemsByOwner(long ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> searchItem(String text, long ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?from={from}&size={size}&text={text}", ownerId, parameters);
    }

    public ResponseEntity<Object> addComment(CommentDto dto, long itemId, long authorId) {
        return post("/" + itemId + "/comment", authorId, dto);
    }
}
