package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Booking;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.BookingRepository;
import com.hackybear.hungry_scan_core.service.interfaces.BookingService;
import com.hackybear.hungry_scan_core.utility.BookingValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;
    private final ExceptionHelper exceptionHelper;

    @Override
    public Booking findById(Long id) throws LocalizedException {
        return bookingRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.bookingService.bookingNotFound", id));
    }

    @Override
    @Transactional
    public void save(Booking booking) throws LocalizedException {
        if (bookingValidator.isValidBooking(booking)) {
            bookingRepository.saveAndFlush(booking);
        } else {
            exceptionHelper.throwLocalizedMessage("error.bookingService.bookingCollides");
        }
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        Booking existingBooking = findById(id);
        bookingRepository.delete(existingBooking);
    }

    @Override
    public Page<Booking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo) {
        return bookingRepository.findAllByDateBetween(pageable, dateFrom, dateTo);
    }

    @Override
    public Long countAll() {
        return bookingRepository.count();
    }

    @Override
    public Long countByDateBetween(LocalDate dateFrom, LocalDate dateTo) {
        return bookingRepository.countAllByDateBetween(dateFrom, dateTo);
    }
}