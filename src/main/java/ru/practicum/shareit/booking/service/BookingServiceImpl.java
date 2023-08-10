package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.OffsetPageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.BookingState;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto addBooking(BookingDto dto, long bookerId) throws BadRequestException, NotFoundException {
        Optional<Item> itemIdDatabase = itemRepository.findById(dto.getItemId());
        Optional<User> booker = userRepository.findById(bookerId);

        if (itemIdDatabase.isEmpty() || booker.isEmpty() || itemIdDatabase.get().getUser().getId() == bookerId) {
            throw new NotFoundException("Пустые параметры бронирования");
        }

        Item item = itemIdDatabase.get();
        if (item.isAvailable() && dto.getEnd().isAfter(dto.getStart())) {
            Booking booking = new Booking();
            booking.setBooker(booker.get());
            booking.setItem(item);
            booking.setStart(dto.getStart());
            booking.setEnd(dto.getEnd());
            booking.setStatus(Status.WAITING);

            Booking savedBooking = bookingRepository.save(booking);
            return bookingMapper.toFullBookingFromBooking(savedBooking);
        } else {
            throw new BadRequestException("Ошибка");
        }
    }

    @Transactional
    @Override
    public BookingDto approveBooking(long bookingId, boolean approved, long itemOwnerId)
            throws BadRequestException, NotFoundException {
        Optional<Booking> bookingIdDatabase = bookingRepository.findById(bookingId);

        if (bookingIdDatabase.isEmpty()) {
            throw new NotFoundException("Бронироввание не найдено");
        }

        Booking booking = bookingIdDatabase.get();

        Item item;
        try {
            item = booking.getItem();
            if (item == null)
                throw new NotFoundException("Не найден владелец вещи");
            if (item.getUser().getId() != itemOwnerId) {
                throw new NotFoundException("Не найден владелец вещи");
            }
        } catch (Exception e) {
            throw new NotFoundException("Не найден владелец вещи");
        }

        User booker = booking.getBooker();
        BookingDto dto = bookingMapper.toBookingDto(booking);
        dto.setId(bookingId);
        Booking updatedBooking;
        Status status;

        if (booking.getStatus() == Status.APPROVED && approved) {
            throw new BadRequestException("Ошибка");
        }

        if (approved) {
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }

        dto.setStatus(status);

        updatedBooking = bookingMapper.toBooking(dto, item, booker);

        return bookingMapper.toFullBookingFromBooking(bookingRepository.save(updatedBooking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBooking(long bookingId, long bookerId) throws NotFoundException {
        Optional<Booking> bookingIdDatabase = bookingRepository.findById(bookingId);

        if (bookingIdDatabase.isEmpty()) {
            throw new NotFoundException("Несуществующее бронирование");
        }

        Booking booking = bookingIdDatabase.get();

        if (booking.getBooker().getId() != bookerId &&
                booking.getItem().getUser().getId() != bookerId) {
            throw new NotFoundException("Бронирование своей вещи невозможно");
        }

        return bookingMapper.toFullBookingFromBooking(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByBookerId(long bookerId, BookingState state, int from, int size) throws NotFoundException {

        if (!userRepository.existsById(bookerId))
            throw new NotFoundException("Не найден хозяин бронирования");

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = new OffsetPageable(from, size, sort);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(bookerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndAfter(bookerId, LocalDateTime.now(),
                        pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfter(bookerId, LocalDateTime.now(),
                        pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeAndStartIsAfter(bookerId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED,
                        pageable);
                break;
            default:
                return Collections.emptyList();
        }

        return bookings.stream()
                .map(booking -> {
                    try {
                        return bookingMapper.toFullBookingFromBooking(booking);
                    } catch (NotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state, int from, int size) throws NotFoundException {

        if (!userRepository.existsById(ownerId))
            throw new NotFoundException("Не найден владелец вещи");

        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = new OffsetPageable(from, size, sort);
        switch (state) {
            case ALL:
                bookings = bookingRepository.bookingsForItem(ownerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.bookingsForItemPast(ownerId, LocalDateTime.now(),
                        pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.bookingsForItemFuture(ownerId, LocalDateTime.now(),
                        pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.bookingsForItemCurrent(ownerId, LocalDateTime.now(),
                        pageable);
                break;
            case WAITING:
                bookings = bookingRepository.bookingsForItemWaiting(ownerId, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.bookingsForItemRejected(ownerId, pageable);
                break;
            default:
                return Collections.emptyList();
        }

        return bookings.stream()
                .map(booking -> {
                    try {
                        return bookingMapper.toFullBookingFromBooking(booking);
                    } catch (NotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}