package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingDto dto,
                                 @RequestHeader("X-Sharer-User-Id") long bookerId)
            throws NotFoundException, BadRequestException {
        log.info("Получен запрос POST /bookings");
        return bookingService.addBooking(dto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") long bookerId)
            throws NotFoundException, BadRequestException {
        log.info("Получен запрос PATCH /bookings/" + bookingId + "?approved=" + approved);
        return bookingService.approveBooking(bookingId, approved, bookerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long bookerId)
            throws NotFoundException {
        log.info("Получен запрос GET /bookings/" + bookingId);
        return bookingService.getBooking(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                     @RequestParam(defaultValue = "ALL") BookingState state)
            throws NotFoundException {
        log.info("Получен запрос GET /bookings?state=" + state.toString());
        return bookingService.getAllBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingItemsByBookerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                         @RequestParam(defaultValue = "ALL") BookingState state)
            throws NotFoundException {
        log.info("Получен запрос GET /bookings/owner?state=" + state.toString());
        return bookingService.getAllBookingByItemsByOwnerId(ownerId, state);
    }
}