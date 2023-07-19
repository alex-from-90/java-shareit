package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.FullBookingDto;
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

    @Transactional
    @Override
    public FullBookingDto addBooking(BookingDto dto, long bookerId) throws BadRequestException, NotFoundException {
        Optional<Item> itemIdDatabase = itemRepository.findById(dto.getItemId());
        Optional<User> booker = userRepository.findById(bookerId);

        if (itemIdDatabase.isEmpty() || booker.isEmpty() || itemIdDatabase.get().getOwnerId() == bookerId) {
            throw new NotFoundException("Пустые параметры бронирования");
        }

        Item item = itemIdDatabase.get();
        if (item.isAvailable() && dto.getEnd().isAfter(dto.getStart())) {
            Booking booking = new Booking();
            booking.setBookerId(bookerId);
            booking.setItemId(dto.getItemId());
            booking.setStart(dto.getStart());
            booking.setEnd(dto.getEnd());
            booking.setStatus(Status.WAITING);

            Booking savedBooking = bookingRepository.save(booking);
            return BookingMapper.toFullBookingFromBooking(savedBooking, Status.WAITING, itemRepository, userRepository);
        } else {
            throw new BadRequestException("Ошибка");
        }
    }

    @Transactional
    @Override
    public FullBookingDto approveBooking(long bookingId, boolean approved, long itemOwnerId)
            throws BadRequestException, NotFoundException {
        Optional<Booking> bookingIdDatabase = bookingRepository.findById(bookingId);

        if (bookingIdDatabase.isEmpty()) {
            throw new NotFoundException("Бронироввание не найдено");
        }

        Booking booking = bookingIdDatabase.get();

        try {
            Item item = itemRepository.findById(booking.getItemId())
                    .orElseThrow(() -> new NotFoundException("Не найден владелец вещи"));
            if (item.getOwnerId() != itemOwnerId) {
                throw new NotFoundException("Не найден владелец вещи");
            }
        } catch (Exception e) {
            throw new NotFoundException("Не найден владелец вещи");
        }

        long bookerId = booking.getBookerId();
        BookingDto dto = BookingMapper.toBookingDto(booking);
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

        updatedBooking = BookingMapper.toBooking(dto, bookerId, status);

        return BookingMapper.toFullBookingFromBooking(bookingRepository.save(updatedBooking), status,
                itemRepository, userRepository);
    }

    @Override
    public FullBookingDto getBooking(long bookingId, long bookerId) throws NotFoundException {
        Optional<Booking> bookingIdDatabase = bookingRepository.findById(bookingId);

        if (bookingIdDatabase.isEmpty()) {
            throw new NotFoundException("Несуществующее бронирование");
        }

        Booking booking = bookingIdDatabase.get();

        if (booking.getBookerId() != bookerId &&
                itemRepository.findById(booking.getItemId())
                        .map(Item::getOwnerId)
                        .orElse(-1L) != bookerId) {
            throw new NotFoundException("Бронирование своей вещи невозможно");
        }

        Status status = booking.getStatus();
        return BookingMapper.toFullBookingFromBooking(booking, status, itemRepository, userRepository);
    }

    @Override
    public List<FullBookingDto> getAllBookingsByBookerId(long bookerId, BookingState state) throws NotFoundException {
        if (userRepository.existsById(bookerId)) {
            List<Booking> bookings;
            switch (state) {
                case ALL:
                    bookings = bookingRepository.findAllByBookerId(bookerId, Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case PAST:
                    bookings = bookingRepository.findByBookerIdAndEndAfter(bookerId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByBookerIdAndStartAfter(bookerId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case CURRENT:
                    bookings = bookingRepository.findByBookerIdAndEndIsBeforeAndStartIsAfter(bookerId,
                            LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                default:
                    return Collections.emptyList();
            }

            return bookings.stream()
                    .map(booking -> {
                        try {
                            return BookingMapper.toFullBookingFromBooking(booking, booking.getStatus(),
                                    itemRepository, userRepository);
                        } catch (NotFoundException e) {

                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Не найден хозяин бронирования");
        }
    }

    @Override
    public List<FullBookingDto> getAllBookingByItemsByOwnerId(long ownerId, BookingState state) throws NotFoundException {
        if (userRepository.existsById(ownerId)) {
            List<Booking> bookings;
            switch (state) {
                case ALL:
                    bookings = bookingRepository.bookingsForItem(ownerId, Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case PAST:
                    bookings = bookingRepository.bookingsForItemPast(ownerId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case FUTURE:
                    bookings = bookingRepository.bookingsForItemFuture(ownerId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case CURRENT:
                    bookings = bookingRepository.bookingsForItemCurrent(ownerId, LocalDateTime.now(),
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case WAITING:
                    bookings = bookingRepository.bookingsForItem(ownerId, Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .filter(booking -> booking.getStatus() == Status.WAITING)
                            .collect(Collectors.toList());
                    break;
                case REJECTED:
                    bookings = bookingRepository.bookingsForItem(ownerId, Sort.by(Sort.Direction.DESC, "start"))
                            .stream()
                            .filter(booking -> booking.getStatus() == Status.REJECTED)
                            .collect(Collectors.toList());
                    break;
                default:
                    return Collections.emptyList();
            }

            return bookings.stream()
                    .map(booking -> {
                        try {
                            return BookingMapper.toFullBookingFromBooking(booking, booking.getStatus(),
                                    itemRepository, userRepository);
                        } catch (NotFoundException e) {

                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Не найден владелец вещи");
        }
    }
}