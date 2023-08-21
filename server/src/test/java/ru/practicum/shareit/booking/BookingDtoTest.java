package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        UserDto user = new UserDto(1L, "test", "test@example.com");
        user.setName("testNameUser");
        user.setEmail("test@mail.com");
        user.setId(1L);

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("testItem")
                .description("test")
                .available(true)
                .build();

        item.setName("testItem");
        item.setDescription("5");
        item.setAvailable(true);
        item.setId(1L);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.of(2022, 1, 15, 10, 11));
        bookingDto.setEnd(LocalDateTime.of(2022, 3, 15, 10, 11));
        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(bookingDto.getId()));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(Math.toIntExact(bookingDto.getItemId()));
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().toString() + ":00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().toString() + ":00");
    }
}