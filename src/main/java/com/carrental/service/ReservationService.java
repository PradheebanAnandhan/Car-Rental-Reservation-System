package com.carrental.service;

import com.carrental.dto.ReservationRequest;
import com.carrental.entity.CarEntity;
import com.carrental.entity.ReservationEntity;
import com.carrental.entity.ReservationStatus;
import com.carrental.entity.UserEntity;
import com.carrental.repository.CarRepository;
import com.carrental.repository.ReservationRepository;
import com.carrental.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            CarRepository carRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @Transactional
    public ReservationEntity createReservation(ReservationRequest request, String username) {
        // Validate request
        request.validate();

        // Validate dates are not in the past
        LocalDateTime now = LocalDateTime.now();
        if (request.getStartDate().isBefore(now)) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CarEntity car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        if (!car.isAvailable()) {
            throw new RuntimeException("Car is not available for reservation");
        }

        // Check if car is already reserved for the requested dates
        boolean isCarReserved = reservationRepository.existsByCarAndDateRange(
                car,
                request.getStartDate(),
                request.getEndDate());

        if (isCarReserved) {
            throw new RuntimeException("Car is already reserved for these dates");
        }

        // Calculate total amount (rounding up partial days)
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days < 1) {
            days = 1; // Minimum one day rental
        } else {
            days += 1; // Add one day because end date is inclusive
        }
        BigDecimal totalPrice = car.getDailyRate().multiply(BigDecimal.valueOf(days));

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUser(user);
        reservation.setCar(car);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus(ReservationStatus.PENDING);

        car.setAvailable(false);
        car.addReservation(reservation);
        carRepository.save(car);

        return reservationRepository.save(reservation);
    }

    public List<ReservationEntity> getAllReservationsByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reservationRepository.findByUser(user);
    }

    public Optional<ReservationEntity> getReservationByIdAndUsername(Long id, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reservationRepository.findByIdAndUser(id, user);
    }

    @Transactional
    public void cancelReservation(Long id, String username) {
        ReservationEntity reservation = getReservationByIdAndUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStartDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel a reservation that has already started");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        CarEntity car = reservation.getCar();
        car.setAvailable(true);
        carRepository.save(car);
        reservationRepository.save(reservation);
    }
}
