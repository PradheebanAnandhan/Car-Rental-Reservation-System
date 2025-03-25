package com.carrental.controller;

import com.carrental.dto.ReservationRequest;
import com.carrental.entity.ReservationEntity;
import com.carrental.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationEntity> createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservationService.createReservation(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<ReservationEntity>> getAllReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservationService.getAllReservationsByUsername(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return reservationService.getReservationByIdAndUsername(id, userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        reservationService.cancelReservation(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
